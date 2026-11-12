const { z } = require('zod');

// Password complexity regex checks
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,72}$/;
const passwordErrorMessage =
  'Password must be 8-72 characters long, containing at least one uppercase letter, one lowercase letter, and one number.';

const phoneNumberValidator = z
  .string()
  .trim()
  .regex(/^\d{10}$/, 'Indian mobile number must be exactly 10 numeric digits');

const passwordValidator = z
  .string()
  .min(8, 'Password must be at least 8 characters long')
  .max(72, 'Password cannot exceed 72 characters')
  .regex(passwordRegex, passwordErrorMessage);

const otpValidator = z
  .string()
  .trim()
  .regex(/^\d{6}$/, 'OTP must be exactly 6 numeric digits');

// 1. Password Login
const loginSchema = z.object({
  phoneNumber: phoneNumberValidator,
  password: z.string().min(1, 'Password is required').max(72)
});

// 2. Registration Request OTP
const registerRequestOtpSchema = z.object({
  name: z.string().trim().min(2, 'Full name must be at least 2 characters').max(50),
  phoneNumber: phoneNumberValidator,
  password: passwordValidator
});

// 3. Registration Verify OTP
const registerVerifyOtpSchema = z.object({
  phoneNumber: phoneNumberValidator,
  otp: otpValidator,
  name: z.string().trim().min(2).max(50),
  password: passwordValidator
});

// 4. Forgot Password Request OTP
const forgotPasswordRequestOtpSchema = z.object({
  phoneNumber: phoneNumberValidator
});

// 5. Forgot Password Reset
const forgotPasswordResetSchema = z.object({
  phoneNumber: phoneNumberValidator,
  otp: otpValidator,
  newPassword: passwordValidator
});

// 6. Change Password (Authenticated)
const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, 'Current password is required').max(72),
  newPassword: passwordValidator
});

// Backward compatibility schemas
const requestOtpSchema = z.object({
  phoneNumber: phoneNumberValidator
});

const verifyOtpSchema = z.object({
  phoneNumber: phoneNumberValidator,
  otp: otpValidator
});

module.exports = {
  loginSchema,
  registerRequestOtpSchema,
  registerVerifyOtpSchema,
  forgotPasswordRequestOtpSchema,
  forgotPasswordResetSchema,
  changePasswordSchema,
  requestOtpSchema,
  verifyOtpSchema
};
