require('dotenv').config();

const env = {
  PORT: parseInt(process.env.PORT, 10) || 5000,
  MONGODB_URI: process.env.MONGODB_URI || 'mongodb://127.0.0.1:27017/medicart',
  JWT_SECRET: process.env.JWT_SECRET || 'medicart_jwt_development_secret_key_2026',
  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || '7d',
  NODE_ENV: process.env.NODE_ENV || 'development',
  isDev: (process.env.NODE_ENV || 'development') === 'development',
  isTest: process.env.NODE_ENV === 'test',
  isProd: process.env.NODE_ENV === 'production',
  // Demonstration / Production Authentication Safety
  OTP_MODE: process.env.OTP_MODE || (process.env.NODE_ENV === 'production' ? 'DEMO' : 'DEV'),
  DEMO_PHONE_NUMBER: process.env.DEMO_PHONE_NUMBER || '9876543210',
  DEMO_OTP_CODE: process.env.DEMO_OTP_CODE || '487210'
};

module.exports = env;
