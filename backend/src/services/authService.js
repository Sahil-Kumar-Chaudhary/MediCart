const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const env = require('../config/env');
const User = require('../models/User');
const Otp = require('../models/Otp');

// Timing-safe HMAC hash for OTP with purpose binding
const hashOtp = (phoneNumber, otp, purpose = 'LOGIN') => {
  return crypto
    .createHmac('sha256', env.JWT_SECRET)
    .update(`${phoneNumber}:${purpose}:${otp}`)
    .digest('hex');
};

// Generate JWT token with tokenVersion for session invalidation
const generateToken = (user) => {
  return jwt.sign(
    {
      userId: user._id,
      phoneNumber: user.phoneNumber,
      tokenVersion: user.tokenVersion || 0
    },
    env.JWT_SECRET,
    { expiresIn: env.JWT_EXPIRES_IN }
  );
};

// Request an OTP for a given purpose ('LOGIN' | 'REGISTRATION' | 'PASSWORD_RESET')
const requestOtp = async (phoneNumber, purpose = 'LOGIN', metadata = null) => {
  const isDemoUser = phoneNumber === env.DEMO_PHONE_NUMBER;
  let rawOtp;

  if (env.isDev || env.isTest) {
    rawOtp = env.DEMO_OTP_CODE;
  } else if (env.OTP_MODE === 'DEMO' && isDemoUser) {
    rawOtp = env.DEMO_OTP_CODE;
  } else {
    rawOtp = Math.floor(100000 + Math.random() * 900000).toString();
  }

  const hashed = hashOtp(phoneNumber, rawOtp, purpose);
  const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutes validity

  // Replace any existing OTP for this phone + purpose
  await Otp.deleteMany({ phoneNumber, purpose });
  await Otp.create({
    phoneNumber,
    otpHash: hashed,
    purpose,
    metadata,
    expiresAt,
    attempts: 0
  });

  return {
    phoneNumber,
    purpose,
    expiresInSeconds: 300,
    // Never leak OTP in production
    ...(env.isDev || env.isTest ? { simulatedOtp: rawOtp } : {}),
    ...(env.isProd ? { isDemoAccount: isDemoUser } : {})
  };
};

// Internal verification of an OTP record
const verifyOtpRecord = async (phoneNumber, otp, purpose = 'LOGIN') => {
  const record = await Otp.findOne({ phoneNumber, purpose });
  if (!record) {
    const error = new Error('No verification code request found or code has expired');
    error.statusCode = 400;
    error.errorCode = 'OTP_NOT_FOUND';
    throw error;
  }

  if (new Date() > record.expiresAt) {
    await Otp.deleteOne({ _id: record._id });
    const error = new Error('Verification code has expired. Please request a new code.');
    error.statusCode = 400;
    error.errorCode = 'OTP_EXPIRED';
    throw error;
  }

  if (record.attempts >= 5) {
    await Otp.deleteOne({ _id: record._id });
    const error = new Error('Maximum verification attempts exceeded. Please request a new code.');
    error.statusCode = 400;
    error.errorCode = 'OTP_ATTEMPTS_EXCEEDED';
    throw error;
  }

  const expectedHash = hashOtp(phoneNumber, otp, purpose);
  const expectedBuf = Buffer.from(expectedHash, 'hex');
  const recordBuf = Buffer.from(record.otpHash, 'hex');
  const isMatch = expectedBuf.length === recordBuf.length && crypto.timingSafeEqual(expectedBuf, recordBuf);

  if (!isMatch) {
    record.attempts += 1;
    await record.save();
    const error = new Error('Invalid verification code');
    error.statusCode = 400;
    error.errorCode = 'INVALID_OTP';
    throw error;
  }

  // Consume OTP (single-use)
  await Otp.deleteOne({ _id: record._id });
  return record;
};

// 1. Password Login
const login = async (phoneNumber, password) => {
  const user = await User.findOne({ phoneNumber });
  if (!user) {
    const error = new Error('Invalid mobile number or password');
    error.statusCode = 401;
    error.errorCode = 'INVALID_CREDENTIALS';
    throw error;
  }

  // Handle legacy users without passwordHash
  if (!user.passwordHash) {
    // If demo user logging in for the first time with Demo@1234, initialize it
    if (phoneNumber === env.DEMO_PHONE_NUMBER && password === 'Demo@1234') {
      user.passwordHash = await User.hashPassword('Demo@1234');
      await user.save();
    } else {
      const error = new Error('Account password not set. Please use Forgot Password to create a password.');
      error.statusCode = 401;
      error.errorCode = 'PASSWORD_NOT_SET';
      throw error;
    }
  }

  const isPasswordValid = await user.comparePassword(password);
  if (!isPasswordValid) {
    const error = new Error('Invalid mobile number or password');
    error.statusCode = 401;
    error.errorCode = 'INVALID_CREDENTIALS';
    throw error;
  }

  const token = generateToken(user);
  return {
    user: user.toJSON(),
    token
  };
};

// 2. Registration Step 1: Request OTP
const registerRequestOtp = async (name, phoneNumber, password) => {
  const existingUser = await User.findOne({ phoneNumber });
  if (existingUser) {
    const error = new Error('An account with this mobile number already exists. Please log in.');
    error.statusCode = 409;
    error.errorCode = 'USER_ALREADY_EXISTS';
    throw error;
  }

  return requestOtp(phoneNumber, 'REGISTRATION', { name });
};

// 3. Registration Step 2: Verify OTP and Create User
const registerVerifyOtp = async (phoneNumber, otp, name, password) => {
  const existingUser = await User.findOne({ phoneNumber });
  if (existingUser) {
    const error = new Error('An account with this mobile number already exists. Please log in.');
    error.statusCode = 409;
    error.errorCode = 'USER_ALREADY_EXISTS';
    throw error;
  }

  await verifyOtpRecord(phoneNumber, otp, 'REGISTRATION');

  const passwordHash = await User.hashPassword(password);
  const user = await User.create({
    name,
    phoneNumber,
    passwordHash,
    tokenVersion: 0
  });

  const token = generateToken(user);
  return {
    user: user.toJSON(),
    token,
    isNewUser: true
  };
};

// 4. Forgot Password Step 1: Request OTP (Generic response to prevent phone enumeration)
const forgotPasswordRequestOtp = async (phoneNumber) => {
  const user = await User.findOne({ phoneNumber });
  let result = {
    phoneNumber,
    expiresInSeconds: 300,
    message: 'If this mobile number is registered with MediCart, a verification OTP has been sent.'
  };

  if (user) {
    const otpResult = await requestOtp(phoneNumber, 'PASSWORD_RESET');
    if (otpResult.simulatedOtp) {
      result.simulatedOtp = otpResult.simulatedOtp;
    }
  }

  return result;
};

// 5. Forgot Password Step 2: Verify OTP and Reset Password
const forgotPasswordReset = async (phoneNumber, otp, newPassword) => {
  const user = await User.findOne({ phoneNumber });
  if (!user) {
    const error = new Error('Invalid reset request or mobile number not registered');
    error.statusCode = 400;
    error.errorCode = 'USER_NOT_FOUND';
    throw error;
  }

  // Verify OTP strictly with purpose 'PASSWORD_RESET'
  await verifyOtpRecord(phoneNumber, otp, 'PASSWORD_RESET');

  // Hash new password and increment tokenVersion to invalidate existing JWTs
  user.passwordHash = await User.hashPassword(newPassword);
  user.tokenVersion = (user.tokenVersion || 0) + 1;
  await user.save();

  return {
    success: true,
    message: 'Password has been reset successfully. Please log in with your new password.'
  };
};

// 6. Change Password (Authenticated)
const changePassword = async (userId, currentPassword, newPassword) => {
  const user = await User.findById(userId);
  if (!user) {
    const error = new Error('User not found');
    error.statusCode = 404;
    error.errorCode = 'USER_NOT_FOUND';
    throw error;
  }

  if (user.passwordHash) {
    const isCurrentValid = await user.comparePassword(currentPassword);
    if (!isCurrentValid) {
      const error = new Error('Current password is incorrect');
      error.statusCode = 400;
      error.errorCode = 'INVALID_CURRENT_PASSWORD';
      throw error;
    }
  }

  user.passwordHash = await User.hashPassword(newPassword);
  user.tokenVersion = (user.tokenVersion || 0) + 1;
  await user.save();

  const token = generateToken(user);
  return {
    user: user.toJSON(),
    token
  };
};

// 7. Backward Compatibility: Legacy OTP Verification
const verifyOtp = async (phoneNumber, otp) => {
  const record = await verifyOtpRecord(phoneNumber, otp, 'LOGIN');
  let user = await User.findOne({ phoneNumber });
  let isNewUser = false;
  if (!user) {
    user = await User.create({
      name: 'Sahil Kumar',
      phoneNumber,
      tokenVersion: 0
    });
    isNewUser = true;
  }

  const token = generateToken(user);
  return {
    user: user.toJSON(),
    token,
    isNewUser
  };
};

module.exports = {
  login,
  requestOtp,
  verifyOtp,
  registerRequestOtp,
  registerVerifyOtp,
  forgotPasswordRequestOtp,
  forgotPasswordReset,
  changePassword,
  generateToken
};
