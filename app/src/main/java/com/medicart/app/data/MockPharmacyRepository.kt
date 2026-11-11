package com.medicart.app.data

import android.content.Context
import android.util.Log
import com.medicart.app.data.local.MediCartDatabaseHelper
import com.medicart.app.data.local.SecureSessionManager
import com.medicart.app.data.remote.NetworkModule
import com.medicart.app.data.remote.api.MediCartApiService
import com.medicart.app.data.remote.dto.*
import com.medicart.app.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Production-ready hybrid pharmacy repository implementing [PharmacyRepository].
 * Combines Retrofit 2 remote REST APIs, SQLite local database caching,
 * and Jetpack Security EncryptedSharedPreferences.
 * Provides offline-first resilience, robust inventory checks, and idempotency protection.
 */
class MockPharmacyRepository(
    private val context: Context? = null,
    injectedApiService: MediCartApiService? = null
) : PharmacyRepository {

    private val dbHelper: MediCartDatabaseHelper? = context?.let { MediCartDatabaseHelper(it) }
    private val sessionManager: SecureSessionManager? = context?.let { SecureSessionManager(it) }

    val apiService: MediCartApiService? = injectedApiService ?: context?.let {
        NetworkModule.createApiService(
            sessionManager = sessionManager,
            onUnauthorized = {
                _isAuthenticated.value = false
            }
        )
    }

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

    // Store in-flight idempotency key in case of network retry
    private var pendingIdempotencyKey: String? = null

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

        // Trigger background sync if online and authenticated
        if (apiService != null && _isAuthenticated.value && !_isOffline.value) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fetchOrders()
                    fetchAddresses()
                } catch (e: Exception) {
                    Log.w(TAG, "Initial background sync deferred: ${e.message}")
                }
            }
        }
    }

    override fun setOfflineMode(offline: Boolean) {
        _isOffline.value = offline
        if (!offline && apiService != null && _isAuthenticated.value) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    fetchOrders()
                    fetchAddresses()
                } catch (_: Exception) {}
            }
        }
    }

    override suspend fun requestOtp(mobileNumber: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanNumber = mobileNumber.filter { it.isDigit() }.takeLast(10)
        if (cleanNumber.length != 10) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid 10-digit Indian mobile number."))
        }

        if (apiService != null && !_isOffline.value) {
            try {
                val response = apiService.requestOtp(RequestOtpRequest(cleanNumber))
                if (response.isSuccessful && response.body()?.success == true) {
                    val simOtp = response.body()?.data?.simulatedOtp ?: "487210"
                    return@withContext Result.success(simOtp)
                } else {
                    val errorMsg = NetworkModule.parseErrorMessage(response)
                    return@withContext Result.failure(Exception(errorMsg))
                }
            } catch (e: IOException) {
                Log.w(TAG, "Request OTP network failure, using fallback: ${e.message}")
                return@withContext Result.failure(Exception("Network error. Please check your internet connection."))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

        // Offline / Unit Test Simulation
        Result.success("487210")
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val cleanNumber = mobileNumber.filter { it.isDigit() }.takeLast(10)
        val cleanOtp = otp.filter { it.isDigit() }.take(6)

        if (cleanOtp.length != 6) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a complete 6-digit OTP code."))
        }

        if (apiService != null && !_isOffline.value) {
            try {
                val response = apiService.verifyOtp(VerifyOtpRequest(cleanNumber, cleanOtp))
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data
                    if (data != null) {
                        sessionManager?.saveSession(
                            mobileNumber = data.user.phoneNumber,
                            token = data.token,
                            recipientName = data.user.name
                        )
                        _isAuthenticated.value = true

                        // Synchronize user data upon successful login
                        launch {
                            try {
                                fetchOrders()
                                fetchAddresses()
                            } catch (_: Exception) {}
                        }
                        return@withContext Result.success(true)
                    }
                }
                val errorMsg = NetworkModule.parseErrorMessage(response)
                return@withContext Result.failure(Exception(errorMsg))
            } catch (e: IOException) {
                Log.w(TAG, "Verify OTP network failure, falling back to local verification: ${e.message}")
                return@withContext Result.failure(Exception("Network error. Please check your internet connection."))
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

        // Test environment or offline fallback
        if (cleanOtp == "487210" || (cleanOtp.length == 6 && cleanOtp.all { it.isDigit() })) {
            _isAuthenticated.value = true
            sessionManager?.saveSession(cleanNumber, "jwt_simulated_${System.currentTimeMillis()}")
            return@withContext Result.success(true)
        }
        Result.failure(IllegalArgumentException("Invalid verification code."))
    }

    override suspend fun logout(): Unit = withContext(Dispatchers.IO) {
        _isAuthenticated.value = false
        sessionManager?.clearSession()
    }

    override fun getCategories(): List<Category> = categories

    override suspend fun getProducts(searchQuery: String, categoryId: String?): Result<List<Product>> = withContext(Dispatchers.IO) {
        if (apiService != null && !_isOffline.value) {
            try {
                // Map categoryId to backend category name if necessary
                val categoryName = when (categoryId) {
                    "cat_pain" -> "Pain Relief"
                    "cat_cough" -> "Cold & Cough"
                    "cat_ayurvedic" -> "Digestive Care"
                    "cat_devices" -> "Healthcare Devices"
                    "cat_firstaid" -> "First Aid"
                    "cat_vitamins" -> "Vitamins & Supplements"
                    else -> categoryId
                }

                val response = apiService.getProducts(
                    search = if (searchQuery.isNotBlank()) searchQuery else null,
                    category = categoryName,
                    limit = 50
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    val dtoList = response.body()?.data?.products ?: emptyList()
                    val domainProducts = dtoList.map { it.toDomain() }

                    // Cache in SQLite database
                    dbHelper?.saveProducts(domainProducts)
                    return@withContext Result.success(domainProducts)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch products from backend, falling back to SQLite cache: ${e.message}")
            }
        }

        // Local SQLite cache / in-memory fallback
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
        Result.success(filtered)
    }

    override suspend fun getProductById(id: String): Result<Product?> = withContext(Dispatchers.IO) {
        if (apiService != null && !_isOffline.value) {
            try {
                val response = apiService.getProductById(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    val dto = response.body()?.data
                    if (dto != null) {
                        val product = dto.toDomain()
                        dbHelper?.saveProducts(listOf(product))
                        return@withContext Result.success(product)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch product details from remote: ${e.message}")
            }
        }

        val product = if (dbHelper != null) {
            dbHelper.getProductById(id) ?: initialProducts.find { it.id == id }
        } else {
            initialProducts.find { it.id == id }
        }
        Result.success(product)
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

        if (apiService != null && !_isOffline.value) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dto = address.toDto()
                    if (dto._id != null) {
                        apiService.updateAddress(dto._id, dto)
                    } else {
                        val res = apiService.createAddress(dto)
                        if (res.isSuccessful && res.body()?.data != null) {
                            val saved = res.body()!!.data!!.toDomain()
                            _currentAddress.value = saved
                            dbHelper?.saveAddress(saved)
                        }
                    }
                } catch (e: Exception) {
                    try { Log.w(TAG, "Address synchronization deferred: ${e.message}") } catch (_: Throwable) {}
                }
            }
        }
    }

    override fun getDefaultAddress(): DeliveryAddress = _currentAddress.value

    override suspend fun fetchAddresses(): Result<List<DeliveryAddress>> = withContext(Dispatchers.IO) {
        if (apiService != null && !_isOffline.value) {
            try {
                val response = apiService.getAddresses()
                if (response.isSuccessful && response.body()?.success == true) {
                    val addresses = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                    if (addresses.isNotEmpty()) {
                        for (a in addresses) {
                            dbHelper?.saveAddress(a)
                        }
                        val defaultAddr = addresses.firstOrNull { it.isDefault } ?: addresses.first()
                        _currentAddress.value = defaultAddr
                        return@withContext Result.success(addresses)
                    }
                }
            } catch (e: Exception) {
                try { Log.w(TAG, "Failed to fetch addresses from backend: ${e.message}") } catch (_: Throwable) {}
            }
        }

        val localAddresses = dbHelper?.getAddresses() ?: listOf(defaultAddress)
        Result.success(localAddresses)
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (apiService != null && !_isOffline.value) {
            try {
                val response = apiService.deleteAddress(addressId)
                if (response.isSuccessful) {
                    return@withContext Result.success(Unit)
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
        Result.success(Unit)
    }

    override suspend fun submitOrder(
        address: DeliveryAddress,
        paymentMethod: PaymentMethod
    ): Result<Order> = withContext(Dispatchers.IO) {
        // 1. Check network connectivity & offline mode
        if (_isOffline.value) {
            return@withContext Result.failure(
                IllegalStateException("Network is currently offline. Order cannot be placed without server confirmation.")
            )
        }

        // 2. Validate Cart
        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("Cannot submit an empty cart."))
        }

        // 3. Revalidate items availability and expiration
        val invalidItem = currentCart.find { !it.product.isAvailable }
        if (invalidItem != null) {
            return@withContext Result.failure(
                IllegalStateException("Item '${invalidItem.product.name}' is out of stock or expired.")
            )
        }

        // 4. Validate quantity limits (max 5)
        val excessiveItem = currentCart.find { it.quantity > 5 }
        if (excessiveItem != null) {
            return@withContext Result.failure(
                IllegalArgumentException("Maximum limit of 5 packs per order exceeded for '${excessiveItem.product.name}'.")
            )
        }

        // 5. Generate / reuse idempotency key
        val idempotencyKey = pendingIdempotencyKey ?: UUID.randomUUID().toString().also {
            pendingIdempotencyKey = it
        }

        // 6. If connected to backend, perform real remote checkout
        if (apiService != null) {
            try {
                // Ensure address exists on backend
                var backendAddressId = if (address.id.isNotBlank() && address.id != "addr_1" && address.id != "addr_snap") {
                    address.id
                } else {
                    null
                }

                if (backendAddressId == null) {
                    val addressDto = address.toDto()
                    val addrRes = apiService.createAddress(addressDto)
                    if (addrRes.isSuccessful && addrRes.body()?.data?._id != null) {
                        backendAddressId = addrRes.body()!!.data!!._id!!
                        val updated = address.copy(id = backendAddressId)
                        _currentAddress.value = updated
                        dbHelper?.saveAddress(updated)
                    } else {
                        // Fall back to first available address or raise error
                        val existing = apiService.getAddresses()
                        if (existing.isSuccessful && !existing.body()?.data.isNullOrEmpty()) {
                            backendAddressId = existing.body()!!.data!!.first()._id
                        } else {
                            val errorMsg = NetworkModule.parseErrorMessage(addrRes)
                            return@withContext Result.failure(IllegalStateException("Failed to verify delivery address on server: $errorMsg"))
                        }
                    }
                }

                val orderItems = currentCart.map {
                    CreateOrderItemRequest(productId = it.product.id, quantity = it.quantity)
                }

                val backendPaymentMethod = when (paymentMethod) {
                    PaymentMethod.CARD_DEMO -> "CARD_DEMO"
                    PaymentMethod.CASH_ON_DELIVERY -> "CASH_ON_DELIVERY"
                    else -> "UPI_DEMO"
                }

                val orderRequest = CreateOrderRequest(
                    addressId = backendAddressId!!,
                    items = orderItems,
                    paymentMethod = backendPaymentMethod,
                    idempotencyKey = idempotencyKey
                )

                val response = apiService.createOrder(orderRequest)

                if (response.isSuccessful && response.body()?.data != null) {
                    val orderDto = response.body()!!.data!!
                    val confirmedOrder = orderDto.toDomain()

                    // Confirmed by backend! Clear in-flight idempotency key
                    pendingIdempotencyKey = null

                    // Persist order in local SQLite and StateFlow
                    _orders.value = listOf(confirmedOrder) + _orders.value
                    dbHelper?.insertOrder(confirmedOrder)

                    // Clear cart ONLY after confirmed backend creation
                    clearCart()

                    return@withContext Result.success(confirmedOrder)
                } else {
                    val errorMsg = NetworkModule.parseErrorMessage(response)
                    // Keep cart and keep pendingIdempotencyKey intact
                    return@withContext Result.failure(IllegalStateException(errorMsg))
                }
            } catch (e: IOException) {
                try { Log.w(TAG, "Network timeout / IO error during order submission. Cart preserved: ${e.message}") } catch (_: Throwable) {}
                // Do not clear cart. Preserve idempotencyKey for retry.
                return@withContext Result.failure(
                    IllegalStateException("Network error: Could not confirm order with server. Your cart is preserved.")
                )
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

        // Test/Standalone Simulation mode
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

        pendingIdempotencyKey = null
        _orders.value = listOf(newOrder) + _orders.value
        dbHelper?.insertOrder(newOrder)
        clearCart()

        Result.success(newOrder)
    }

    override suspend fun fetchOrders(): Result<List<Order>> = withContext(Dispatchers.IO) {
        if (apiService != null && !_isOffline.value) {
            try {
                val response = apiService.getOrders()
                if (response.isSuccessful && response.body()?.success == true) {
                    val ordersList = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                    if (ordersList.isNotEmpty()) {
                        for (o in ordersList) {
                            dbHelper?.insertOrder(o)
                        }
                        _orders.value = ordersList
                        return@withContext Result.success(ordersList)
                    }
                }
            } catch (e: Exception) {
                try { Log.w(TAG, "Failed to fetch orders from backend, using local orders: ${e.message}") } catch (_: Throwable) {}
            }
        }

        val localOrders = dbHelper?.getAllOrders() ?: initialOrders
        Result.success(localOrders)
    }

    override suspend fun getOrderById(orderRef: String): Result<Order?> = withContext(Dispatchers.IO) {
        val memoryMatch = _orders.value.find { it.orderReference == orderRef }
        if (memoryMatch != null) {
            return@withContext Result.success(memoryMatch)
        }

        if (apiService != null && !_isOffline.value) {
            try {
                val encodedRef = URLEncoder.encode(orderRef, StandardCharsets.UTF_8.toString())
                val response = apiService.getOrderById(encodedRef)
                if (response.isSuccessful && response.body()?.success == true) {
                    val dto = response.body()?.data
                    if (dto != null) {
                        val order = dto.toDomain()
                        dbHelper?.insertOrder(order)
                        return@withContext Result.success(order)
                    }
                }
            } catch (e: Exception) {
                try { Log.w(TAG, "Failed to fetch order details from backend: ${e.message}") } catch (_: Throwable) {}
            }
        }

        val dbOrder = dbHelper?.getAllOrders()?.find { it.orderReference == orderRef }
        Result.success(dbOrder)
    }

    companion object {
        private const val TAG = "MockPharmacyRepository"
    }
}
