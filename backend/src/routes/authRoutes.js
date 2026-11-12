const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { requireAuth } = require('../middleware/authMiddleware');
const validate = require('../middleware/validate');
const { authRateLimiter, loginRateLimiter } = require('../middleware/rateLimiter');
const {
  loginSchema,
  registerRequestOtpSchema,
  registerVerifyOtpSchema,
  forgotPasswordRequestOtpSchema,
  forgotPasswordResetSchema,
  changePasswordSchema,
  requestOtpSchema,
  verifyOtpSchema
} = require('../validators/authValidator');

// 1. Password Login
router.post('/login', loginRateLimiter, validate(loginSchema), authController.handleLogin);

// 2. Registration Flow
router.post(
  '/register/request-otp',
  authRateLimiter,
  validate(registerRequestOtpSchema),
  authController.handleRegisterRequestOtp
);
router.post(
  '/register/verify-otp',
  authRateLimiter,
  validate(registerVerifyOtpSchema),
  authController.handleRegisterVerifyOtp
);

// 3. Forgot Password Flow
router.post(
  '/forgot-password/request-otp',
  authRateLimiter,
  validate(forgotPasswordRequestOtpSchema),
  authController.handleForgotPasswordRequestOtp
);
router.post(
  '/forgot-password/reset',
  authRateLimiter,
  validate(forgotPasswordResetSchema),
  authController.handleForgotPasswordReset
);

// 4. Change Password (Protected)
router.post(
  '/change-password',
  requireAuth,
  validate(changePasswordSchema),
  authController.handleChangePassword
);

// 5. User Profile (Protected)
router.get('/me', requireAuth, authController.handleGetMe);

// 6. Legacy OTP Endpoints (Preserved for compatibility)
router.post(
  '/request-otp',
  authRateLimiter,
  validate(requestOtpSchema),
  authController.handleRequestOtp
);
router.post(
  '/verify-otp',
  authRateLimiter,
  validate(verifyOtpSchema),
  authController.handleVerifyOtp
);

module.exports = router;
