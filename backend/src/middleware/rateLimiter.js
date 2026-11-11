const rateLimit = require('express-rate-limit');
const env = require('../config/env');
const { errorResponse } = require('../utils/responseEnvelope');

// In test environment, disable rate limiting to prevent test interference
const authRateLimiter = env.isTest
  ? (req, res, next) => next()
  : rateLimit({
      windowMs: 15 * 60 * 1000, // 15 minutes
      max: 10, // Max 10 requests per 15 minutes per IP
      standardHeaders: true,
      legacyHeaders: false,
      handler: (req, res) => {
        return errorResponse(
          res,
          429,
          'Too many OTP requests from this IP. Please try again after 15 minutes.',
          'TOO_MANY_REQUESTS'
        );
      }
    });

module.exports = { authRateLimiter };
