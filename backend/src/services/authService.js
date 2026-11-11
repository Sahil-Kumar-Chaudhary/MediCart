const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const env = require('../config/env');
const User = require('../models/User');
const Otp = require('../models/Otp');

const hashOtp = (phoneNumber, otp) => {
  return crypto
    .createHmac('sha256', env.JWT_SECRET)
    .update(`${phoneNumber}:${otp}`)
    .digest('hex');
};

const requestOtp = async (phoneNumber) => {
  // Use predictable code in dev/test for reproducible testing, or generate 6-digit random
  const rawOtp = (env.isDev || env.isTest) ? '487210' : Math.floor(100000 + Math.random() * 900000).toString();
  const hashed = hashOtp(phoneNumber, rawOtp);
  const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5 minutes validity

  // Upsert OTP record
  await Otp.deleteMany({ phoneNumber });
  await Otp.create({
    phoneNumber,
    otpHash: hashed,
    expiresAt,
    attempts: 0
  });

  return {
    phoneNumber,
    expiresInSeconds: 300,
    // Strictly development only simulated OTP provider
    ...(env.isDev || env.isTest ? { simulatedOtp: rawOtp } : {})
  };
};

const verifyOtp = async (phoneNumber, otp) => {
  const record = await Otp.findOne({ phoneNumber });
  if (!record) {
    const error = new Error('No OTP request found for this mobile number or code expired');
    error.statusCode = 400;
    error.errorCode = 'OTP_EXPIRED_OR_NOT_FOUND';
    throw error;
  }

  if (new Date() > record.expiresAt) {
    await Otp.deleteOne({ _id: record._id });
    const error = new Error('OTP has expired. Please request a new code.');
    error.statusCode = 400;
    error.errorCode = 'OTP_EXPIRED';
    throw error;
  }

  const expectedHash = hashOtp(phoneNumber, otp);
  if (record.otpHash !== expectedHash) {
    record.attempts += 1;
    await record.save();
    const error = new Error('Invalid verification code');
    error.statusCode = 400;
    error.errorCode = 'INVALID_OTP';
    throw error;
  }

  // OTP verified, consume it
  await Otp.deleteOne({ _id: record._id });

  // Find or create user
  let user = await User.findOne({ phoneNumber });
  let isNewUser = false;
  if (!user) {
    user = await User.create({
      name: 'Sahil Kumar',
      phoneNumber
    });
    isNewUser = true;
  }

  // Generate JWT token
  const token = jwt.sign(
    { userId: user._id, phoneNumber: user.phoneNumber },
    env.JWT_SECRET,
    { expiresIn: env.JWT_EXPIRES_IN }
  );

  return {
    user: {
      _id: user._id,
      name: user.name,
      phoneNumber: user.phoneNumber,
      createdAt: user.createdAt
    },
    token,
    isNewUser
  };
};

module.exports = { requestOtp, verifyOtp };
