package com.medicart.app.data.remote.dto

data class AddressDto(
    val _id: String? = null,
    val userId: String? = null,
    val recipientName: String,
    val phoneNumber: String,
    val houseNumber: String,
    val locality: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val addressType: String? = "HOME",
    val isDefault: Boolean? = false,
    val createdAt: String? = null
)
