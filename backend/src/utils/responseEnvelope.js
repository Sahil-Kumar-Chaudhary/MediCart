/**
 * Standard API Response Envelope
 */
const successResponse = (res, statusCode = 200, message = 'Success', data = null) => {
  const payload = {
    success: true,
    message
  };
  if (data !== null && data !== undefined) {
    payload.data = data;
  }
  return res.status(statusCode).json(payload);
};

const errorResponse = (res, statusCode = 400, message = 'An error occurred', errorCode = 'BAD_REQUEST', details = null) => {
  const payload = {
    success: false,
    message,
    errorCode
  };
  if (details !== null && details !== undefined) {
    payload.details = details;
  }
  return res.status(statusCode).json(payload);
};

module.exports = { successResponse, errorResponse };
