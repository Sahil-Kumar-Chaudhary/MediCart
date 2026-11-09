package com.medicart.app.model

/**
 * Item placed in customer cart with quantity validation
 */
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.priceInr * quantity

    val totalSavings: Double
        get() = (product.mrpInr - product.priceInr) * quantity
}

data class CartSummary(
    val items: List<CartItem>,
    val deliveryFee: Double = 30.0,
    val packagingFee: Double = 14.0
) {
    val itemCount: Int
        get() = items.sumOf { it.quantity }

    val itemTotal: Double
        get() = items.sumOf { it.totalPrice }

    val totalSavings: Double
        get() = items.sumOf { it.totalSavings }

    val finalTotal: Double
        get() = if (items.isEmpty()) 0.0 else itemTotal + deliveryFee + packagingFee
}
