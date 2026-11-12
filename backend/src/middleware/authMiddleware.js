const jwt = require('jsonwebtoken');
const env = require('../config/env');
const User = require('../models/User');
const { errorResponse } = require('../utils/responseEnvelope');

const requireAuth = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return errorResponse(res, 401, 'Authorization token required', 'UNAUTHORIZED');
    }

    const token = authHeader.split(' ')[1];
    let decoded;
    try {
      decoded = jwt.verify(token, env.JWT_SECRET);
    } catch (err) {
      if (err.name === 'TokenExpiredError') {
        return errorResponse(res, 401, 'Authentication token has expired', 'TOKEN_EXPIRED');
      }
      return errorResponse(res, 401, 'Invalid authentication token', 'INVALID_TOKEN');
    }

    const user = await User.findById(decoded.userId).select('-__v');
    if (!user) {
      return errorResponse(res, 401, 'User account no longer exists', 'USER_NOT_FOUND');
    }

    // Check token version to invalidate sessions after password reset
    if (decoded.tokenVersion !== undefined && decoded.tokenVersion < (user.tokenVersion || 0)) {
      return errorResponse(
        res,
        401,
        'Session has expired due to password reset. Please log in again.',
        'SESSION_REVOKED'
      );
    }

    req.user = user;
    next();
  } catch (error) {
    return errorResponse(res, 500, 'Authentication error', 'INTERNAL_SERVER_ERROR');
  }
};

module.exports = { requireAuth };
