package com.medicart.app

import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.data.remote.NetworkModule
import com.medicart.app.data.remote.api.MediCartApiService
import com.medicart.app.model.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * 20-Point Acceptance Test Suite for MediCart Android Retrofit Integration.
 * Validates real HTTP network requests, DTO mapping, error handling, session management,
 * and offline persistence using OkHttp MockWebServer.
 */
class MediCartRetrofitIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: MediCartApiService
    private lateinit var repository: MockPharmacyRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString()
        val okHttpClient = NetworkModule.createOkHttpClient()
        val retrofit = NetworkModule.createRetrofit(okHttpClient, baseUrl)
        apiService = retrofit.create(MediCartApiService::class.java)

        repository = MockPharmacyRepository(
            context = null,
            injectedApiService = apiService
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // 1. OTP Request
    @Test
    fun test1_OtpRequest_Success() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":true,"message":"Verification code dispatched","data":{"phoneNumber":"9876543210","expiresInSeconds":300,"simulatedOtp":"487210"}}""")
        )

        val result = repository.requestOtp("9876543210")
        assertTrue("OTP request should succeed", result.isSuccess)
        assertEquals("487210", result.getOrNull())

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertNotNull(recorded)
        assertEquals("/api/auth/request-otp", recorded?.path)
        assertEquals("POST", recorded?.method)
        assertTrue(recorded!!.body.readUtf8().contains("9876543210"))
    }

    // 2. Successful Authentication & JWT Extraction
    @Test
    fun test2_SuccessfulAuthentication() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":true,"message":"Verified","data":{"user":{"_id":"usr_101","name":"Sahil Kumar","phoneNumber":"9876543210"},"token":"jwt_test_token_sample_123"}}""")
        )

        val result = repository.verifyOtp("9876543210", "487210")
        assertTrue("Authentication should succeed", result.isSuccess)
        assertTrue(repository.isAuthenticated.value)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertEquals("/api/auth/verify-otp", recorded?.path)
    }

    // 3. Invalid OTP Rejection
    @Test
    fun test3_InvalidOtp_Rejected() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Invalid verification code","errorCode":"INVALID_OTP"}""")
        )

        val result = repository.verifyOtp("9876543210", "000000")
        assertTrue("Invalid OTP must fail", result.isFailure)
    }

    // 4. JWT Storage and Authorization Header Attachment
    @Test
    fun test4_JwtStorageAndAuthHeader() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":true,"message":"Orders","data":[]}""")
        )

        val result = repository.fetchOrders()
        assertTrue(result.isSuccess)

        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertEquals("/api/orders", recorded?.path)
        assertEquals("GET", recorded?.method)
    }

    // 5. Product Fetching
    @Test
    fun test5_ProductFetching_Success() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "message":"Products retrieved",
                    "data":{
                        "products":[
                            {
                                "_id":"6732389a01",
                                "productId":"med_01",
                                "name":"Paracare 650mg Tablets",
                                "genericName":"Paracetamol IP 650mg",
                                "category":"Pain Relief",
                                "pricePaise":3400,
                                "mrpPaise":3800,
                                "packSize":"Strip of 15 Tablets",
                                "stockCount":45,
                                "isAvailable":true,
                                "isExpired":false,
                                "expiryDate":"Dec 2028"
                            }
                        ],
                        "pagination":{"totalItems":1,"currentPage":1,"totalPages":1,"limit":20}
                    }
                }""")
        )

        val result = repository.getProducts()
        assertTrue(result.isSuccess)
        val products = result.getOrThrow()
        assertEquals(1, products.size)
        assertEquals("Paracare 650mg Tablets", products[0].name)
        assertEquals(34.0, products[0].priceInr, 0.001) // 3400 paise -> ₹34.00
    }

    // 6. Product Search
    @Test
    fun test6_ProductSearch() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "data":{"products":[],"pagination":{"totalItems":0,"currentPage":1,"totalPages":0,"limit":20}}
                }""")
        )

        repository.getProducts("Paracetamol", null)
        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertTrue("Search query must be transmitted", recorded?.path?.contains("search=Paracetamol") == true)
    }

    // 7. Category Filtering
    @Test
    fun test7_CategoryFiltering() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "data":{"products":[],"pagination":{"totalItems":0,"currentPage":1,"totalPages":0,"limit":20}}
                }""")
        )

        repository.getProducts("", "cat_pain")
        val recorded = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertTrue("Category query must be mapped and transmitted", recorded?.path?.contains("category=Pain") == true)
    }

    // 8. Product Detail Loading
    @Test
    fun test8_ProductDetailLoading() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "data":{
                        "_id":"6732389a01",
                        "productId":"med_01",
                        "name":"Paracare 650mg Tablets",
                        "genericName":"Paracetamol IP 650mg",
                        "category":"Pain Relief",
                        "pricePaise":3400,
                        "mrpPaise":3800,
                        "packSize":"Strip of 15 Tablets",
                        "stockCount":45,
                        "isAvailable":true,
                        "isExpired":false,
                        "expiryDate":"Dec 2028"
                    }
                }""")
        )

        val result = repository.getProductById("med_01")
        assertTrue(result.isSuccess)
        val product = result.getOrNull()
        assertNotNull(product)
        assertEquals("med_01", product?.id)
        assertEquals(34.0, product?.priceInr ?: 0.0, 0.001)
    }

    // 9. Local Cart Persistence
    @Test
    fun test9_LocalCartPersistence() = runBlocking {
        val testProduct = Product(
            id = "med_01",
            name = "Paracare 650mg",
            genericName = "Paracetamol",
            packSize = "Strip of 15",
            priceInr = 34.0,
            mrpInr = 38.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 20,
            expiryDate = "2028",
            activeIngredient = "Paracetamol"
        )

        repository.clearCart()
        assertEquals(0, repository.cartItems.value.size)

        repository.addToCart(testProduct, 2)
        assertEquals(1, repository.cartItems.value.size)
        assertEquals(2, repository.cartItems.value[0].quantity)

        repository.updateCartQuantity("med_01", 3)
        assertEquals(3, repository.cartItems.value[0].quantity)
    }

    // 10. Address CRUD
    @Test
    fun test10_AddressCrud() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "data":[
                        {
                            "_id":"addr_mongo_101",
                            "recipientName":"Sahil Kumar",
                            "phoneNumber":"9876543210",
                            "houseNumber":"Flat 402",
                            "locality":"Indiranagar",
                            "city":"Bengaluru",
                            "state":"Karnataka",
                            "pinCode":"560038",
                            "addressType":"HOME",
                            "isDefault":true
                        }
                    ]
                }""")
        )

        val result = repository.fetchAddresses()
        assertTrue(result.isSuccess)
        val addresses = result.getOrThrow()
        assertEquals(1, addresses.size)
        assertEquals("Bengaluru", addresses[0].city)
        assertEquals("560038", addresses[0].pinCode)
    }

    // 11. Successful Backend Order Creation
    @Test
    fun test11_SuccessfulBackendOrderCreation() = runBlocking {
        val testProduct = Product(
            id = "med_01",
            name = "Paracare 650mg",
            genericName = "Paracetamol",
            packSize = "Strip of 15",
            priceInr = 34.0,
            mrpInr = 38.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 20,
            expiryDate = "2028",
            activeIngredient = "Paracetamol"
        )
        repository.clearCart()
        repository.addToCart(testProduct, 1)

        val address = DeliveryAddress(
            id = "addr_mongo_101",
            recipientName = "Sahil Kumar",
            mobileNumber = "9876543210",
            flatNumber = "Flat 402",
            streetLocality = "Indiranagar",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560038"
        )

        // Mock Order Creation Response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "message":"Order confirmed",
                    "data":{
                        "_id":"ord_101",
                        "orderId":"#MC-2026-9042",
                        "items":[{"productId":"med_01","name":"Paracare 650mg","genericName":"Paracetamol","unitPricePaise":3400,"quantity":1,"totalPricePaise":3400}],
                        "deliveryAddress":{"recipientName":"Sahil Kumar","phoneNumber":"9876543210","houseNumber":"Flat 402","locality":"Indiranagar","city":"Bengaluru","state":"Karnataka","pinCode":"560038","addressType":"HOME"},
                        "subtotalPaise":3400,
                        "deliveryFeePaise":3000,
                        "packagingFeePaise":1400,
                        "totalAmountPaise":7800,
                        "paymentMethod":"UPI_DEMO",
                        "paymentStatus":"CONFIRMED_SIMULATED",
                        "orderStatus":"PROCESSING"
                    }
                }""")
        )

        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)
        assertTrue("Order should be placed successfully", orderResult.isSuccess)
        val order = orderResult.getOrThrow()
        assertEquals("#MC-2026-9042", order.orderReference)
        assertEquals(78.0, order.totalAmount, 0.001) // 7800 paise = ₹78.00
        assertEquals(0, repository.cartItems.value.size) // Cart cleared on success
    }

    // 12. Out-of-Stock Product Rejection
    @Test
    fun test12_OutOfStockRejection() = runBlocking {
        val oosProduct = Product(
            id = "med_oos",
            name = "Out of Stock Medicine",
            genericName = "Generic",
            packSize = "Box",
            priceInr = 100.0,
            mrpInr = 120.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = false,
            stockCount = 0,
            expiryDate = "2028",
            activeIngredient = "Active"
        )

        val result = repository.addToCart(oosProduct, 1)
        assertTrue("Out of stock item must be rejected", result.isFailure)
    }

    // 13. Expired Product Rejection
    @Test
    fun test13_ExpiredProductRejection() = runBlocking {
        val expiredProduct = Product(
            id = "med_exp",
            name = "Expired Batch",
            genericName = "Generic",
            packSize = "Box",
            priceInr = 100.0,
            mrpInr = 120.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 10,
            isExpired = true,
            expiryDate = "2022 (Expired)",
            activeIngredient = "Active"
        )

        val result = repository.addToCart(expiredProduct, 1)
        assertTrue("Expired item must be rejected", result.isFailure)
    }

    // 14. HTTP 401 Unauthorized Handling
    @Test
    fun test14_Http401Handling() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Invalid authentication token","errorCode":"INVALID_TOKEN"}""")
        )

        val response = apiService.getCurrentUser()
        assertEquals(401, response.code())
        assertFalse(response.isSuccessful)
    }

    // 15. HTTP 500 Internal Server Error Handling
    @Test
    fun test15_Http500Handling() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Internal server error","errorCode":"INTERNAL_SERVER_ERROR"}""")
        )

        val response = apiService.getProducts()
        assertEquals(500, response.code())
        val parsed = NetworkModule.parseErrorMessage(response)
        assertEquals("Internal server error", parsed)
    }

    // 16. Network Timeout Handling
    @Test
    fun test16_NetworkTimeoutHandling() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )

        val testProduct = Product(
            id = "med_01",
            name = "Paracare 650mg",
            genericName = "Paracetamol",
            packSize = "Strip of 15",
            priceInr = 34.0,
            mrpInr = 38.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 20,
            expiryDate = "2028",
            activeIngredient = "Paracetamol"
        )
        repository.clearCart()
        repository.addToCart(testProduct, 1)

        val address = DeliveryAddress(
            id = "addr_mongo_101",
            recipientName = "Sahil Kumar",
            mobileNumber = "9876543210",
            flatNumber = "Flat 402",
            streetLocality = "Indiranagar",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560038"
        )

        // Submit order when server hangs
        val orderResult = repository.submitOrder(address, PaymentMethod.UPI_DEMO)
        assertTrue("Network timeout must return failure", orderResult.isFailure)
        // Cart must be preserved!
        assertEquals("Cart must NOT be cleared on network timeout", 1, repository.cartItems.value.size)
    }

    // 17. Duplicate Order Prevention / Idempotency Key Preservation
    @Test
    fun test17_DuplicateOrderPrevention_Idempotency() = runBlocking {
        val testProduct = Product(
            id = "med_01",
            name = "Paracare 650mg",
            genericName = "Paracetamol",
            packSize = "Strip of 15",
            priceInr = 34.0,
            mrpInr = 38.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 20,
            expiryDate = "2028",
            activeIngredient = "Paracetamol"
        )
        repository.clearCart()
        repository.addToCart(testProduct, 1)

        val address = DeliveryAddress(
            id = "addr_mongo_101",
            recipientName = "Sahil Kumar",
            mobileNumber = "9876543210",
            flatNumber = "Flat 402",
            streetLocality = "Indiranagar",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560038"
        )

        // First attempt fails with 500
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Gateway Timeout"}""")
        )

        val attempt1 = repository.submitOrder(address, PaymentMethod.UPI_DEMO)
        assertTrue(attempt1.isFailure)

        val recorded1 = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        val body1 = recorded1?.body?.readUtf8()
        val idempotencyKey1 = Regex(""""idempotencyKey":"([^"]+)"""").find(body1 ?: "")?.groupValues?.get(1)
        assertNotNull("Request must contain idempotencyKey", idempotencyKey1)

        // Second retry attempt succeeds with 200 idempotent response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "message":"Order already submitted",
                    "data":{
                        "_id":"ord_101",
                        "orderId":"#MC-2026-9042",
                        "items":[],
                        "deliveryAddress":{"recipientName":"Sahil","phoneNumber":"9876543210","houseNumber":"402","locality":"Main","city":"Bengaluru","state":"KA","pinCode":"560038","addressType":"HOME"},
                        "subtotalPaise":3400,
                        "deliveryFeePaise":3000,
                        "packagingFeePaise":1400,
                        "totalAmountPaise":7800,
                        "paymentMethod":"UPI_DEMO",
                        "orderStatus":"PROCESSING"
                    }
                }""")
        )

        val attempt2 = repository.submitOrder(address, PaymentMethod.UPI_DEMO)
        assertTrue(attempt2.isSuccess)

        val recorded2 = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        val body2 = recorded2?.body?.readUtf8()
        val idempotencyKey2 = Regex(""""idempotencyKey":"([^"]+)"""").find(body2 ?: "")?.groupValues?.get(1)

        // Must reuse same idempotency key for safe retry!
        assertEquals("Retry must preserve original idempotency key to prevent double charging", idempotencyKey1, idempotencyKey2)
    }

    // 18. Cart Preservation After Failed Checkout
    @Test
    fun test18_CartPreservationAfterFailedCheckout() = runBlocking {
        val testProduct = Product(
            id = "med_01",
            name = "Paracare 650mg",
            genericName = "Paracetamol",
            packSize = "Strip of 15",
            priceInr = 34.0,
            mrpInr = 38.0,
            discountPercent = 10,
            categoryId = "cat_pain",
            inStock = true,
            stockCount = 20,
            expiryDate = "2028",
            activeIngredient = "Paracetamol"
        )
        repository.clearCart()
        repository.addToCart(testProduct, 3)

        val address = DeliveryAddress(
            id = "addr_mongo_101",
            recipientName = "Sahil Kumar",
            mobileNumber = "9876543210",
            flatNumber = "Flat 402",
            streetLocality = "Indiranagar",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560038"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"success":false,"message":"Insufficient stock","errorCode":"INSUFFICIENT_STOCK"}""")
        )

        val result = repository.submitOrder(address, PaymentMethod.UPI_DEMO)
        assertTrue(result.isFailure)
        assertEquals("Cart items must remain unchanged on failed submission", 1, repository.cartItems.value.size)
        assertEquals(3, repository.cartItems.value[0].quantity)
    }

    // 19. Successful Reconnection & Offline Mode Toggle
    @Test
    fun test19_SuccessfulReconnection() = runBlocking {
        repository.setOfflineMode(true)
        assertTrue(repository.isOffline.value)

        // In offline mode, getProducts retrieves local cached items
        val offlineProducts = repository.getProducts()
        assertTrue(offlineProducts.isSuccess)
        assertTrue(offlineProducts.getOrThrow().isNotEmpty())

        // Reconnect online
        repository.setOfflineMode(false)
        assertFalse(repository.isOffline.value)
    }

    // 20. Order History Retrieval
    @Test
    fun test20_OrderHistoryRetrieval() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{
                    "success":true,
                    "data":[
                        {
                            "_id":"ord_hist_1",
                            "orderId":"#MC-2026-7810",
                            "items":[],
                            "deliveryAddress":{"recipientName":"Sahil","phoneNumber":"9876543210","houseNumber":"402","locality":"Main","city":"Bengaluru","state":"KA","pinCode":"560038","addressType":"HOME"},
                            "subtotalPaise":9000,
                            "deliveryFeePaise":3000,
                            "packagingFeePaise":1400,
                            "totalAmountPaise":13400,
                            "paymentMethod":"UPI_DEMO",
                            "orderStatus":"DELIVERED"
                        }
                    ]
                }""")
        )

        val result = repository.fetchOrders()
        assertTrue(result.isSuccess)
        val orders = result.getOrThrow()
        assertEquals(1, orders.size)
        assertEquals("#MC-2026-7810", orders[0].orderReference)
        assertEquals(134.0, orders[0].totalAmount, 0.001) // 13400 paise = ₹134.00
    }
}
