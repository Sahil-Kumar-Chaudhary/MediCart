package com.medicart.app.data.remote.dto

import com.medicart.app.model.*

/**
 * Domain mappers translating between backend REST DTOs and Android domain models.
 * Enforces safe integer-paise to Indian rupee decimal conversions.
 */

fun ProductDto.toDomain(): Product {
    val priceInr = pricePaise / 100.0
    val mrpInr = mrpPaise / 100.0
    val discount = if (mrpPaise > pricePaise && mrpPaise > 0) {
        (((mrpPaise - pricePaise) * 100) / mrpPaise).toInt()
    } else {
        0
    }

    return Product(
        id = productId,
        name = name,
        genericName = genericName,
        packSize = packSize,
        priceInr = priceInr,
        mrpInr = mrpInr,
        discountPercent = discount,
        categoryId = when {
            category.contains("pain", ignoreCase = true) -> "cat_pain"
            category.contains("cough", ignoreCase = true) || category.contains("cold", ignoreCase = true) -> "cat_cough"
            category.contains("digest", ignoreCase = true) -> "cat_ayurvedic"
            category.contains("device", ignoreCase = true) -> "cat_devices"
            category.contains("first", ignoreCase = true) -> "cat_firstaid"
            category.contains("vitamin", ignoreCase = true) -> "cat_vitamins"
            else -> "cat_pain"
        },
        inStock = isAvailable && stockCount > 0 && !isExpired,
        stockCount = stockCount,
        isExpired = isExpired,
        expiryDate = expiryDate,
        activeIngredient = genericName,
        storageInstruction = description ?: "Store below 25°C in a dry place away from sunlight.",
        returnableDays = 7,
        iconEmoji = when {
            category.contains("pain", ignoreCase = true) -> "💊"
            category.contains("cough", ignoreCase = true) -> "🫁"
            category.contains("device", ignoreCase = true) -> "🌡️"
            category.contains("vitamin", ignoreCase = true) -> "🛡️"
            category.contains("first", ignoreCase = true) -> "🩺"
            else -> "🌿"
        }
    )
}

fun AddressDto.toDomain(): DeliveryAddress {
    val cleanType = when (addressType?.uppercase()) {
        "WORK" -> AddressType.WORK
        "OTHER" -> AddressType.OTHER
        else -> AddressType.HOME
    }

    return DeliveryAddress(
        id = _id ?: "addr_${System.currentTimeMillis()}",
        recipientName = recipientName,
        mobileNumber = if (phoneNumber.startsWith("+91")) phoneNumber else "+91 $phoneNumber",
        flatNumber = houseNumber,
        streetLocality = locality,
        city = city,
        state = state,
        pinCode = pinCode,
        addressType = cleanType,
        isDefault = isDefault ?: false
    )
}

fun DeliveryAddress.toDto(): AddressDto {
    val cleanDigits = mobileNumber.filter { it.isDigit() }.takeLast(10)
    return AddressDto(
        _id = if (id.startsWith("addr_")) null else id,
        recipientName = recipientName,
        phoneNumber = if (cleanDigits.length == 10) cleanDigits else "9876543210",
        houseNumber = flatNumber,
        locality = streetLocality,
        city = city,
        state = state,
        pinCode = pinCode,
        addressType = addressType.name,
        isDefault = isDefault
    )
}

fun OrderItemSnapshotDto.toDomain(): CartItem {
    val priceInr = unitPricePaise / 100.0
    val product = Product(
        id = productId,
        name = name,
        genericName = genericName,
        packSize = "Standard Pack",
        priceInr = priceInr,
        mrpInr = priceInr,
        discountPercent = 0,
        categoryId = "cat_pain",
        inStock = true,
        stockCount = 10,
        expiryDate = "N/A",
        activeIngredient = genericName
    )
    return CartItem(product = product, quantity = quantity)
}

fun DeliveryAddressSnapshotDto.toDomain(): DeliveryAddress {
    val cleanType = when (addressType.uppercase()) {
        "WORK" -> AddressType.WORK
        "OTHER" -> AddressType.OTHER
        else -> AddressType.HOME
    }

    return DeliveryAddress(
        id = "addr_snap",
        recipientName = recipientName,
        mobileNumber = if (phoneNumber.startsWith("+91")) phoneNumber else "+91 $phoneNumber",
        flatNumber = houseNumber,
        streetLocality = locality,
        city = city,
        state = state,
        pinCode = pinCode,
        addressType = cleanType,
        isDefault = false
    )
}

fun OrderDto.toDomain(): Order {
    val mappedItems = items.map { it.toDomain() }
    val mappedAddress = deliveryAddress.toDomain()
    val totalAmount = totalAmountPaise / 100.0

    val mappedPayment = when (paymentMethod) {
        "CARD_DEMO" -> PaymentMethod.CARD_DEMO
        "CASH_ON_DELIVERY" -> PaymentMethod.CASH_ON_DELIVERY
        else -> PaymentMethod.UPI_DEMO
    }

    val mappedStatus = when (orderStatus?.uppercase()) {
        "DELIVERED" -> OrderStatus.DELIVERED
        "CANCELLED" -> OrderStatus.CANCELLED
        "SHIPPED" -> OrderStatus.DISPATCHED
        "PACKING" -> OrderStatus.PACKING
        else -> OrderStatus.PROCESSING
    }

    return Order(
        orderReference = orderId,
        createdAt = createdAt ?: "Today",
        items = mappedItems,
        address = mappedAddress,
        paymentMethod = mappedPayment,
        totalAmount = totalAmount,
        status = mappedStatus,
        estimatedDelivery = "Tomorrow by 11:00 AM",
        paymentTransactionId = "TXN-SIM-${orderId.replace("#", "")}"
    )
}
