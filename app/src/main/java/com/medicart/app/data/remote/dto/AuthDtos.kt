package com.medicart.app.data.remote.dto

data class RequestOtpRequest(
    val phoneNumber: String
)

data class RequestOtpResponseData(
    val phoneNumber: String,
    val expiresInSeconds: Int,
    val simulatedOtp: String? = null
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
