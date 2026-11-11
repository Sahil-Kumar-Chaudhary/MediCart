const env = require('../config/env');
const { errorResponse } = require('../utils/responseEnvelope');

const notFoundHandler = (req, res) => {
  return errorResponse(res, 404, `Route ${req.method} ${req.originalUrl} not found`, 'ROUTE_NOT_FOUND');
};

const errorHandler = (err, req, res, next) => {
  if (!env.isTest) {
    console.error(`[Unhandled Error] ${err.message}`, err.stack);
  }

  // Mongoose CastError (e.g. invalid ObjectId)
  if (err.name === 'CastError') {
    return errorResponse(res, 400, `Invalid ID format for ${err.path}`, 'INVALID_ID');
  }

  // Mongoose Duplicate Key Error
  if (err.code === 11000) {
    const field = Object.keys(err.keyValue || {})[0] || 'field';
    return errorResponse(res, 409, `Duplicate value for ${field}`, 'DUPLICATE_KEY');
  }

  const statusCode = err.statusCode || 500;
  const message = err.message || 'Internal server error';
  const errorCode = err.errorCode || 'INTERNAL_SERVER_ERROR';

  return errorResponse(
    res,
    statusCode,
    message,
    errorCode,
    env.isDev ? { stack: err.stack } : null
  );
};

module.exports = { notFoundHandler, errorHandler };
