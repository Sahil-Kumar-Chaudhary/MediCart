const authService = require('../services/authService');
const { successResponse, errorResponse } = require('../utils/responseEnvelope');

const handleRequestOtp = async (req, res, next) => {
  try {
    const { phoneNumber } = req.body;
    const result = await authService.requestOtp(phoneNumber);
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
    return successResponse(
      res,
      200,
      'Authentication successful',
      result
    );
  } catch (error) {
    next(error);
  }
};

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

module.exports = {
  handleRequestOtp,
  handleVerifyOtp,
  handleGetMe
};
