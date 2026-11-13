package com.medicart.app.data.remote.dto

// 1. Password Login
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

data class LoginResponseData(
    val user: UserDto,
    val token: String
)

// 2. Registration
data class RegisterRequestOtpRequest(
    val name: String,
    val phoneNumber: String,
    val password: String
)

data class RegisterVerifyOtpRequest(
    val phoneNumber: String,
    val otp: String,
    val name: String,
    val password: String
)

// 3. Forgot Password
data class ForgotPasswordRequestOtpRequest(
    val phoneNumber: String
)

data class ForgotPasswordResetRequest(
    val phoneNumber: String,
    val otp: String,
    val newPassword: String
)

data class PasswordResetResponseData(
    val success: Boolean,
    val message: String
)

// 4. Change Password
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

// Legacy & Generic OTP
data class RequestOtpRequest(
    val phoneNumber: String
)

data class RequestOtpResponseData(
    val phoneNumber: String,
    val expiresInSeconds: Int? = null,
    val simulatedOtp: String? = null,
    val message: String? = null
)

data class VerifyOtpRequest(
    val phoneNumber: String,
    val otp: String
)

data class UserDto(
    val _id: String,
    val name: String,
    val phoneNumber: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class VerifyOtpResponseData(
    val user: UserDto,
    val token: String,
    val isNewUser: Boolean? = null
)
