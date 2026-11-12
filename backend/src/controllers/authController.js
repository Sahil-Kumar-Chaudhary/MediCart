const authService = require('../services/authService');
const { successResponse } = require('../utils/responseEnvelope');

// 1. Password Login
const handleLogin = async (req, res, next) => {
  try {
    const { phoneNumber, password } = req.body;
    const result = await authService.login(phoneNumber, password);
    return successResponse(res, 200, 'Login successful', result);
  } catch (error) {
    next(error);
  }
};

// 2. Registration Step 1: Request OTP
const handleRegisterRequestOtp = async (req, res, next) => {
  try {
    const { name, phoneNumber, password } = req.body;
    const result = await authService.registerRequestOtp(name, phoneNumber, password);
    return successResponse(
      res,
      200,
      'Registration verification code sent successfully',
      result
    );
  } catch (error) {
    next(error);
  }
};

// 3. Registration Step 2: Verify OTP and Create User
const handleRegisterVerifyOtp = async (req, res, next) => {
  try {
    const { phoneNumber, otp, name, password } = req.body;
    const result = await authService.registerVerifyOtp(phoneNumber, otp, name, password);
    return successResponse(res, 201, 'Account registered successfully', result);
  } catch (error) {
    next(error);
  }
};

// 4. Forgot Password Step 1: Request OTP
const handleForgotPasswordRequestOtp = async (req, res, next) => {
  try {
    const { phoneNumber } = req.body;
    const result = await authService.forgotPasswordRequestOtp(phoneNumber);
    return successResponse(
      res,
      200,
      result.message || 'If this mobile number is registered, a reset code has been sent',
      result
    );
  } catch (error) {
    next(error);
  }
};

// 5. Forgot Password Step 2: Reset Password
const handleForgotPasswordReset = async (req, res, next) => {
  try {
    const { phoneNumber, otp, newPassword } = req.body;
    const result = await authService.forgotPasswordReset(phoneNumber, otp, newPassword);
    return successResponse(res, 200, result.message, result);
  } catch (error) {
    next(error);
  }
};

// 6. Change Password (Authenticated)
const handleChangePassword = async (req, res, next) => {
  try {
    const { currentPassword, newPassword } = req.body;
    const result = await authService.changePassword(req.user._id, currentPassword, newPassword);
    return successResponse(res, 200, 'Password changed successfully', result);
  } catch (error) {
    next(error);
  }
};

// 7. Get Authenticated User Profile
const handleGetMe = async (req, res, next) => {
  try {
    return successResponse(
      res,
      200,
      'User profile retrieved successfully',
      {
        _id: req.user._id,
        name: req.user.name,
        phoneNumber: req.user.phoneNumber,
        createdAt: req.user.createdAt
      }
    );
  } catch (error) {
    next(error);
  }
};

// Legacy OTP endpoints for backward compatibility
const handleRequestOtp = async (req, res, next) => {
  try {
    const { phoneNumber } = req.body;
    const result = await authService.requestOtp(phoneNumber, 'LOGIN');
    return successResponse(
      res,
      200,
      'OTP sent successfully via simulated SMS provider',
      result
    );
  } catch (error) {
    next(error);
  }
};

const handleVerifyOtp = async (req, res, next) => {
  try {
    const { phoneNumber, otp } = req.body;
    const result = await authService.verifyOtp(phoneNumber, otp);
    return successResponse(res, 200, 'Authentication successful', result);
  } catch (error) {
    next(error);
  }
};

module.exports = {
  handleLogin,
  handleRegisterRequestOtp,
  handleRegisterVerifyOtp,
  handleForgotPasswordRequestOtp,
  handleForgotPasswordReset,
  handleChangePassword,
  handleGetMe,
  handleRequestOtp,
  handleVerifyOtp
};
