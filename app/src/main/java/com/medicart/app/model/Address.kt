package com.medicart.app.model

enum class AddressType {
    HOME, WORK, OTHER
}

/**
 * Standard Indian delivery address format with 6-digit PIN validation
 */
data class DeliveryAddress(
    val id: String,
    val recipientName: String,
    val mobileNumber: String,
    val flatNumber: String,
    val streetLocality: String,
    val landmark: String = "",
    val city: String,
    val state: String,
    val pinCode: String,
    val addressType: AddressType = AddressType.HOME,
    val isDefault: Boolean = false
) {
    val fullAddressLine: String
        get() = "$flatNumber, $streetLocality, $city, $state - $pinCode"

    val isValidPinCode: Boolean
        get() = pinCode.length == 6 && pinCode.all { it.isDigit() } && !pinCode.startsWith("0")
}
