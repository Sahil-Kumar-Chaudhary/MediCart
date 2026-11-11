package com.medicart.app.data

import com.medicart.app.model.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Domain interface for MediCart Pharmacy operations.
 * Decouples presentation from data sources, local SQLite persistence and remote Retrofit APIs.
 */
interface PharmacyRepository {
    val isAuthenticated: StateFlow<Boolean>
    val isOffline: StateFlow<Boolean>
    val cartItems: StateFlow<List<CartItem>>
    val orders: StateFlow<List<Order>>
    val currentAddress: StateFlow<DeliveryAddress>

    fun setOfflineMode(offline: Boolean)
    suspend fun requestOtp(mobileNumber: String): Result<String> = Result.success("487210")
    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Boolean>
    suspend fun logout()

    fun getCategories(): List<Category>
    suspend fun getProducts(searchQuery: String = "", categoryId: String? = null): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product?>

    fun addToCart(product: Product, quantity: Int = 1): Result<Unit>
    fun updateCartQuantity(productId: String, newQuantity: Int): Result<Unit>
    fun clearCart()

    fun updateAddress(address: DeliveryAddress)
    fun getDefaultAddress(): DeliveryAddress
    suspend fun fetchAddresses(): Result<List<DeliveryAddress>> = Result.success(listOf(getDefaultAddress()))
    suspend fun deleteAddress(addressId: String): Result<Unit> = Result.success(Unit)

    suspend fun submitOrder(
        address: DeliveryAddress,
        paymentMethod: PaymentMethod
    ): Result<Order>

    suspend fun fetchOrders(): Result<List<Order>> = Result.success(orders.value)
    suspend fun getOrderById(orderRef: String): Result<Order?> = Result.success(orders.value.find { it.orderReference == orderRef })
}
