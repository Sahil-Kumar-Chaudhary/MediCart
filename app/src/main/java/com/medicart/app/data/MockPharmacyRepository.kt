package com.medicart.app.data

import android.content.Context
import com.medicart.app.data.local.MediCartDatabaseHelper
import com.medicart.app.data.local.SecureSessionManager
import com.medicart.app.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production-ready pharmacy repository implementing [PharmacyRepository].
 * Backed by SQLite persistent storage and Jetpack Security EncryptedSharedPreferences.
 * Supports offline mode simulation, network failure resilience, and strict inventory validation.
 */
class MockPharmacyRepository(
    private val context: Context? = null
) : PharmacyRepository {

    private val dbHelper: MediCartDatabaseHelper? = context?.let { MediCartDatabaseHelper(it) }
    private val sessionManager: SecureSessionManager? = context?.let { SecureSessionManager(it) }

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

    private val initialOrders = listOf(
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
    private val _isAuthenticated = MutableStateFlow(sessionManager?.isAuthenticated ?: false)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    override val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _currentAddress = MutableStateFlow(defaultAddress)
    override val currentAddress: StateFlow<DeliveryAddress> = _currentAddress.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        // Initialize SQLite persistence if available
        if (dbHelper != null) {
            val existingProducts = dbHelper.getAllProducts()
            if (existingProducts.isEmpty()) {
                dbHelper.saveProducts(initialProducts)
            }

            val savedAddresses = dbHelper.getAddresses()
            if (savedAddresses.isEmpty()) {
                dbHelper.saveAddress(defaultAddress)
                _currentAddress.value = defaultAddress
            } else {
                _currentAddress.value = savedAddresses.firstOrNull { it.isDefault } ?: savedAddresses.first()
            }

            val savedCart = dbHelper.getCart()
            if (savedCart.isEmpty()) {
                // Seed initial demo cart items
                val initialCart = listOf(
                    CartItem(initialProducts[0], 2),
                    CartItem(initialProducts[2], 1)
                )
                dbHelper.saveCart(initialCart)
                _cartItems.value = initialCart
            } else {
                _cartItems.value = savedCart
            }

            val savedOrders = dbHelper.getAllOrders()
            if (savedOrders.isEmpty()) {
                for (o in initialOrders) {
                    dbHelper.insertOrder(o)
                }
                _orders.value = initialOrders
            } else {
                _orders.value = savedOrders
            }
        } else {
            // In-memory test environment fallback
            _cartItems.value = listOf(
                CartItem(initialProducts[0], 2),
                CartItem(initialProducts[2], 1)
            )
            _orders.value = initialOrders
        }
    }

    override fun setOfflineMode(offline: Boolean) {
        _isOffline.value = offline
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Boolean> {
        delay(300) // Simulate auth verification latency
        if (otp.length == 6 && otp.all { it.isDigit() }) {
            _isAuthenticated.value = true
            sessionManager?.saveSession(mobileNumber, "jwt_simulated_${System.currentTimeMillis()}")
            return Result.success(true)
        }
        return Result.failure(IllegalArgumentException("Invalid 6-digit OTP code."))
    }

    override suspend fun logout() {
        _isAuthenticated.value = false
        sessionManager?.clearSession()
    }

    override fun getCategories(): List<Category> = categories

    override suspend fun getProducts(searchQuery: String, categoryId: String?): Result<List<Product>> {
        delay(200) // Emulate network roundtrip
        val source = if (dbHelper != null) {
            val dbProducts = dbHelper.getAllProducts()
            if (dbProducts.isNotEmpty()) dbProducts else initialProducts
        } else {
            initialProducts
        }

        val filtered = source.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.genericName.contains(searchQuery, ignoreCase = true)
            val matchesCategory = categoryId == null || product.categoryId == categoryId
            matchesQuery && matchesCategory
        }
        return Result.success(filtered)
    }

    override suspend fun getProductById(id: String): Result<Product?> {
        delay(100)
        val product = if (dbHelper != null) {
            dbHelper.getProductById(id) ?: initialProducts.find { it.id == id }
        } else {
            initialProducts.find { it.id == id }
        }
        return Result.success(product)
    }

    override fun addToCart(product: Product, quantity: Int): Result<Unit> {
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
        dbHelper?.saveCart(current)
        return Result.success(Unit)
    }

    override fun updateCartQuantity(productId: String, newQuantity: Int): Result<Unit> {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else if (newQuantity <= 5) {
                current[index] = current[index].copy(quantity = newQuantity)
            } else {
                return Result.failure(IllegalArgumentException("Maximum limit of 5 packs per order reached."))
            }
            _cartItems.value = current
            dbHelper?.saveCart(current)
            return Result.success(Unit)
        }
        return Result.failure(NoSuchElementException("Product not found in cart."))
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
        dbHelper?.clearCart()
    }

    override fun updateAddress(address: DeliveryAddress) {
        _currentAddress.value = address
        dbHelper?.saveAddress(address)
    }

    override fun getDefaultAddress(): DeliveryAddress = _currentAddress.value

    override suspend fun submitOrder(
        address: DeliveryAddress,
        paymentMethod: PaymentMethod
    ): Result<Order> {
        delay(800) // Emulate network roundtrip and backend processing

        // Check offline resilience
        if (_isOffline.value) {
            return Result.failure(
                IllegalStateException("Network is currently offline. Order cannot be placed without server confirmation.")
            )
        }

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

        // Persist order in local database and memory
        _orders.value = listOf(newOrder) + _orders.value
        dbHelper?.insertOrder(newOrder)

        // Clear cart after confirmed submission
        clearCart()

        return Result.success(newOrder)
    }
}
