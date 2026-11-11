const { errorResponse } = require('../utils/responseEnvelope');

const validate = (schema) => (req, res, next) => {
  try {
    const parsed = schema.parse(req.body);
    req.body = parsed;
    next();
  } catch (error) {
    const errorDetails = error.errors
      ? error.errors.map(err => ({ field: err.path.join('.'), message: err.message }))
      : null;
    return errorResponse(res, 400, 'Validation failed', 'VALIDATION_ERROR', errorDetails);
  }
};

module.exports = validate;
