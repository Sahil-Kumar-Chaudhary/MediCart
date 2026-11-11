package com.medicart.app.data.remote.dto

data class CreateOrderItemRequest(
    val productId: String,
    val quantity: Int
)

data class CreateOrderRequest(
    val addressId: String,
    val items: List<CreateOrderItemRequest>,
    val paymentMethod: String,
    val idempotencyKey: String? = null
)

data class OrderItemSnapshotDto(
    val productId: String,
    val name: String,
    val genericName: String,
    val unitPricePaise: Long,
    val quantity: Int,
    val totalPricePaise: Long
)

data class DeliveryAddressSnapshotDto(
    val recipientName: String,
    val phoneNumber: String,
    val houseNumber: String,
    val locality: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val addressType: String
)

data class OrderDto(
    val _id: String? = null,
    val orderId: String,
    val userId: String? = null,
    val items: List<OrderItemSnapshotDto>,
    val deliveryAddress: DeliveryAddressSnapshotDto,
    val subtotalPaise: Long,
    val deliveryFeePaise: Long,
    val packagingFeePaise: Long,
    val totalAmountPaise: Long,
    val paymentMethod: String,
    val paymentStatus: String? = null,
    val orderStatus: String? = "PROCESSING",
    val idempotencyKey: String? = null,
    val createdAt: String? = null
)
