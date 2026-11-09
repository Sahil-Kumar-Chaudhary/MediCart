package com.medicart.app.data

import com.medicart.app.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production-ready mock pharmacy repository.
 * Emulates backend network requests, inventory validation, stock rules and order persistence.
 */
class MockPharmacyRepository {

    private val categories = listOf(
        Category("cat_pain", "Pain Relief", "💊", 42),
        Category("cat_cough", "Cough & Cold", "🫁", 38),
        Category("cat_firstaid", "First Aid", "🩺", 29),
        Category("cat_vitamins", "Vitamins", "🛡️", 54),
        Category("cat_ayurvedic", "Ayurvedic", "🌿", 31),
        Category("cat_devices", "Health Devices", "🌡️", 18)
    )

    private val initialProducts = listOf(
        Product(
            id = "med_01",
            name = "Paracare 650mg Tablets",
            genericName = "Paracetamol IP 650mg",
            packSize = "Strip of 15 Tablets",
            priceInr = 34.0,
            mrpInr = 38.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 45,
            expiryDate = "Dec 2028",
            activeIngredient = "Paracetamol IP 650 mg",
            iconEmoji = "💊"
        ),
        Product(
            id = "med_02",
            name = "Cetriz-L 10mg Tablets",
            genericName = "Levocetirizine Dihydrochloride 10mg",
            packSize = "Strip of 10 Tablets",
            priceInr = 22.0,
            mrpInr = 28.0,
            discountPercent = 21,
            categoryId = "cat_cough",
            inStock = true,
            stockCount = 20,
            expiryDate = "Nov 2028",
            activeIngredient = "Levocetirizine 10 mg",
            iconEmoji = "💊"
        ),
        Product(
            id = "med_03",
            name = "AyurTulsi Cough Relief Herbal Syrup",
            genericName = "Tulsi, Vasaka, Mulethi & Honey Blend",
            packSize = "Bottle of 100 ml",
            priceInr = 85.0,
            mrpInr = 99.0,
            discountPercent = 14,
            categoryId = "cat_cough",
            inStock = true,
            stockCount = 18,
            expiryDate = "Mar 2028",
            activeIngredient = "Ocimum sanctum extract, Adhatoda vasica",
            iconEmoji = "🌿"
        ),
        Product(
            id = "med_04",
            name = "Panto-D SR Capsules",
            genericName = "Pantoprazole 40mg + Domperidone 30mg",
            packSize = "Strip of 10 Capsules",
            priceInr = 95.0,
            mrpInr = 118.0,
            discountPercent = 19,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 30,
            expiryDate = "Jul 2028",
            activeIngredient = "Pantoprazole 40mg, Domperidone 30mg",
            iconEmoji = "💊"
        ),
        Product(
            id = "med_05",
            name = "OmniTherm Instant Digital Thermometer",
            genericName = "Clinical Fast Digital Thermometer",
            packSize = "1 Unit with storage case",
            priceInr = 199.0,
            mrpInr = 260.0,
            discountPercent = 23,
            categoryId = "cat_devices",
            inStock = true,
            stockCount = 8,
            expiryDate = "N/A (Device)",
            activeIngredient = "Medical grade sensor",
            iconEmoji = "🌡️"
        ),
        Product(
            id = "med_06",
            name = "VitaZinc Immunity Chewables",
            genericName = "Vitamin C 500mg + Zinc 10mg",
            packSize = "Bottle of 60 Chewables",
            priceInr = 240.0,
            mrpInr = 290.0,
            discountPercent = 17,
            categoryId = "cat_vitamins",
            inStock = true,
            stockCount = 12,
            expiryDate = "Oct 2028",
            activeIngredient = "Ascorbic Acid 500mg, Zinc Gluconate 10mg",
            iconEmoji = "🛡️"
        ),
        // OUT OF STOCK ITEM DEMO
        Product(
            id = "med_07",
            name = "N95 Healthcare Respirator Mask",
            genericName = "5-Layer Particulate Filter Mask",
            packSize = "Pack of 5 Masks",
            priceInr = 150.0,
            mrpInr = 200.0,
            discountPercent = 25,
            categoryId = "cat_firstaid",
            inStock = false,
            stockCount = 0,
            expiryDate = "Dec 2029",
            activeIngredient = "Meltblown non-woven fabric",
            iconEmoji = "😷"
        ),
        // EXPIRED ITEM DEMO
        Product(
            id = "med_08",
            name = "ColdRelief Night Syrup (Expired Batch)",
            genericName = "Fictional Night Decongestant",
            packSize = "Bottle of 100 ml",
            priceInr = 45.0,
            mrpInr = 55.0,
            discountPercent = 18,
            categoryId = "cat_cough",
            inStock = false,
            stockCount = 5,
            isExpired = true,
            expiryDate = "Aug 2026 (Expired)",
            activeIngredient = "Batch Withheld",
            iconEmoji = "🛑"
        )
    )

    private val defaultAddress = DeliveryAddress(
        id = "addr_1",
        recipientName = "Sahil Kumar",
        mobileNumber = "+91 98765 43210",
        flatNumber = "Flat 402, Block B, Shanti Vihar",
        streetLocality = "12th Main Road, HAL 2nd Stage",
        city = "Bengaluru",
        state = "Karnataka",
        pinCode = "560038",
        addressType = AddressType.HOME,
        isDefault = true
    )

    private val initialOrders = mutableListOf(
        Order(
            orderReference = "#MC-2026-7810",
            createdAt = "05 Nov 2026, 04:12 PM",
            items = listOf(
                CartItem(initialProducts[0], 2),
                CartItem(initialProducts[2], 1)
            ),
            address = defaultAddress,
            paymentMethod = PaymentMethod.UPI_DEMO,
            totalAmount = 216.0,
            status = OrderStatus.DELIVERED
        ),
        Order(
            orderReference = "#MC-2026-5190",
            createdAt = "28 Oct 2026, 11:20 AM",
            items = listOf(
                CartItem(initialProducts[1], 2)
            ),
            address = defaultAddress,
            paymentMethod = PaymentMethod.UPI_DEMO,
            totalAmount = 72.0,
            status = OrderStatus.CANCELLED
        )
    )

    // Reactive State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun verifyOtp(mobileNumber: String, otp: String): Boolean {
        // Requires 6-digit code
        if (otp.length == 6 && otp.all { it.isDigit() }) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(initialProducts[0], 2), // Paracare 650
            CartItem(initialProducts[2], 1)  // AyurTulsi
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(initialOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun getCategories(): List<Category> = categories

    suspend fun getProducts(searchQuery: String = "", categoryId: String? = null): List<Product> {
        delay(200) // Simulate fast network roundtrip
        return initialProducts.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.genericName.contains(searchQuery, ignoreCase = true)
            val matchesCategory = categoryId == null || product.categoryId == categoryId
            matchesQuery && matchesCategory
        }
    }

    suspend fun getProductById(id: String): Product? {
        delay(100)
        return initialProducts.find { it.id == id }
    }

    // Cart Operations with business validation
    fun addToCart(product: Product, quantity: Int = 1): Result<Unit> {
        if (!product.isAvailable) {
            return Result.failure(IllegalStateException("Cannot add out of stock or expired item."))
        }
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val newQty = current[index].quantity + quantity
            if (newQty > 5) {
                return Result.failure(IllegalArgumentException("Maximum limit of 5 packs per order reached."))
            }
            current[index] = current[index].copy(quantity = newQty)
        } else {
            if (quantity > 5) {
                return Result.failure(IllegalArgumentException("Maximum limit of 5 packs per order reached."))
            }
            current.add(CartItem(product, quantity))
        }
        _cartItems.value = current
        return Result.success(Unit)
    }

    fun updateCartQuantity(productId: String, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else if (newQuantity <= 5) {
                current[index] = current[index].copy(quantity = newQuantity)
            }
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Order Placement (simulated backend call)
    suspend fun submitOrder(
        address: DeliveryAddress,
        paymentMethod: PaymentMethod
    ): Result<Order> {
        delay(800) // Emulate real backend database transaction

        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) {
            return Result.failure(IllegalStateException("Cannot submit an empty cart."))
        }

        // Validate items are still in stock before confirming
        val invalidItem = currentCart.find { !it.product.isAvailable }
        if (invalidItem != null) {
            return Result.failure(IllegalStateException("Item '${invalidItem.product.name}' is no longer available."))
        }

        val cartSummary = CartSummary(currentCart)
        val newOrder = Order(
            orderReference = "#MC-2026-${(1000..9999).random()}",
            createdAt = "Today, 10:24 PM",
            items = currentCart,
            address = address,
            paymentMethod = paymentMethod,
            totalAmount = cartSummary.finalTotal,
            status = OrderStatus.PROCESSING
        )

        // Persist order in history and clear cart
        _orders.value = listOf(newOrder) + _orders.value
        _cartItems.value = emptyList()

        return Result.success(newOrder)
    }

    fun getDefaultAddress(): DeliveryAddress = defaultAddress
}
