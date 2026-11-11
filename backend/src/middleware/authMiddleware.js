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

    req.user = user;
    next();
  } catch (error) {
    return errorResponse(res, 500, 'Authentication error', 'INTERNAL_SERVER_ERROR');
  }
};

module.exports = { requireAuth };
