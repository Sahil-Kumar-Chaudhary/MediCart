const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const { requireAuth } = require('../middleware/authMiddleware');
const validate = require('../middleware/validate');
const { authRateLimiter } = require('../middleware/rateLimiter');
const { requestOtpSchema, verifyOtpSchema } = require('../validators/authValidator');

// Public endpoints
router.post('/request-otp', authRateLimiter, validate(requestOtpSchema), authController.handleRequestOtp);
router.post('/verify-otp', authRateLimiter, validate(verifyOtpSchema), authController.handleVerifyOtp);

// Protected endpoint
router.get('/me', requireAuth, authController.handleGetMe);

module.exports = router;
