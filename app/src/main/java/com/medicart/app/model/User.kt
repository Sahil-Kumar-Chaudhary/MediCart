package com.medicart.app.model

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी (Hindi)")
}

data class User(
    val name: String,
    val phoneNumber: String
)

data class UserProfile(
    val id: String,
    val fullName: String,
    val mobileNumber: String,
    val email: String,
    val preferredLanguage: AppLanguage = AppLanguage.ENGLISH,
    val savedAddresses: List<DeliveryAddress> = emptyList()
)
