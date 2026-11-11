const { z } = require('zod');

const requestOtpSchema = z.object({
  phoneNumber: z
    .string()
    .trim()
    .regex(/^\d{10}$/, 'Indian mobile number must be exactly 10 numeric digits')
});

const verifyOtpSchema = z.object({
  phoneNumber: z
    .string()
    .trim()
    .regex(/^\d{10}$/, 'Indian mobile number must be exactly 10 numeric digits'),
  otp: z
    .string()
    .trim()
    .regex(/^\d{6}$/, 'OTP must be exactly 6 numeric digits')
});

module.exports = { requestOtpSchema, verifyOtpSchema };
