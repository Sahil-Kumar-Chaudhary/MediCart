package com.medicart.app.model

enum class OrderStatus {
    PROCESSING,
    PACKING,
    DISPATCHED,
    DELIVERED,
    CANCELLED
}

enum class PaymentMethod(val label: String, val isSimulated: Boolean = true) {
    UPI_DEMO("UPI (Simulated Demo)"),
    CASH_ON_DELIVERY("Cash on Delivery"),
    CARD_DEMO("Card (Simulated Demo)")
}

data class Order(
    val orderReference: String, // e.g. #MC-2026-8942
    val createdAt: String,
    val items: List<CartItem>,
    val address: DeliveryAddress,
    val paymentMethod: PaymentMethod,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.PROCESSING,
    val estimatedDelivery: String = "Tomorrow by 11:00 AM",
    val paymentTransactionId: String = "TXN-SIM-${System.currentTimeMillis() % 100000}"
)
