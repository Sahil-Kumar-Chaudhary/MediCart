package com.medicart.app.model

/**
 * Fictional pharmacy medicine item
 */
data class Product(
    val id: String,
    val name: String,
    val genericName: String,
    val packSize: String,
    val priceInr: Double,
    val mrpInr: Double,
    val discountPercent: Int,
    val categoryId: String,
    val inStock: Boolean,
    val stockCount: Int,
    val isExpired: Boolean = false,
    val expiryDate: String,
    val activeIngredient: String,
    val storageInstruction: String = "Store below 25°C in a dry place away from sunlight.",
    val returnableDays: Int = 7,
    val iconEmoji: String = "💊"
) {
    val isAvailable: Boolean
        get() = inStock && stockCount > 0 && !isExpired
}
