# MediCart Android API Contract Specification

This document provides the strict client-server contract for the upcoming Kotlin + Jetpack Compose Android integration.

---

## 1. Client-Side Parsing Principles

1. **Standard Envelope Pattern**:
   Every response uses `ApiResponse<T>`:
   ```kotlin
   data class ApiResponse<T>(
       val success: Boolean,
       val message: String,
       val data: T? = null,
       val errorCode: String? = null,
       val errors: List<ValidationError>? = null
   )
   ```
2. **Currency Conversion**:
   All monetary amounts received from the API are `Long` or `Int` in paise.
   Client helper function:
   ```kotlin
   fun Long.toRupeesString(): String {
       val rupees = this / 100
       val paise = this % 100
       return if (paise == 0L) "₹$rupees" else "₹$rupees.%02d".format(paise)
   }
   ```
3. **Idempotency**:
   Android clients should supply a UUID or timestamp string in the `idempotencyKey` field of `CreateOrderRequest` to avoid duplicate checkout charges when retrying on poor network connectivity.

---

## 2. Kotlin Data Transfer Objects (DTOs)

### Authentication Models
```kotlin
// POST /api/auth/request-otp
data class RequestOtpRequest(
    val phoneNumber: String // 10-digit format e.g. "9876543210"
)

data class RequestOtpResponseData(
    val phoneNumber: String,
    val expiresInSeconds: Int,
    val simulatedOtp: String? = null
)

// POST /api/auth/verify-otp
data class VerifyOtpRequest(
    val phoneNumber: String,
    val otp: String // 6-digit string e.g. "487210"
)

data class VerifyOtpResponseData(
    val user: UserDto,
    val token: String,
    val isNewUser: Boolean
)

data class UserDto(
    val _id: String,
    val name: String,
    val phoneNumber: String,
    val createdAt: String,
    val updatedAt: String? = null
)
```

---

### Product Catalogue Models
```kotlin
data class ProductDto(
    val _id: String,
    val productId: String,
    val name: String,
    val genericName: String,
    val description: String,
    val category: String,
    val pricePaise: Long,
    val mrpPaise: Long,
    val packSize: String,
    val stockCount: Int,
    val isAvailable: Boolean,
    val isExpired: Boolean,
    val expiryDate: String,
    val imageUrl: String
)

data class ProductListResponseData(
    val products: List<ProductDto>,
    val pagination: PaginationDto
)

data class PaginationDto(
    val totalItems: Int,
    val currentPage: Int,
    val totalPages: Int,
    val limit: Int
)
```

---

### Address Models
```kotlin
data class AddressDto(
    val _id: String? = null,
    val userId: String? = null,
    val recipientName: String,
    val phoneNumber: String,
    val houseNumber: String,
    val locality: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val addressType: String = "HOME", // "HOME", "WORK", "OTHER"
    val isDefault: Boolean = false
)
```

---

### Order Models
```kotlin
data class CreateOrderItemRequest(
    val productId: String,
    val quantity: Int // Min 1, Max 5
)

data class CreateOrderRequest(
    val addressId: String,
    val items: List<CreateOrderItemRequest>,
    val paymentMethod: String, // "UPI_DEMO", "CASH_ON_DELIVERY", "CARD_DEMO"
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
    val _id: String,
    val orderId: String, // "#MC-2026-XXXX"
    val userId: String,
    val items: List<OrderItemSnapshotDto>,
    val deliveryAddress: DeliveryAddressSnapshotDto,
    val subtotalPaise: Long,
    val deliveryFeePaise: Long,
    val packagingFeePaise: Long,
    val totalAmountPaise: Long,
    val paymentMethod: String,
    val paymentStatus: String, // "CONFIRMED_SIMULATED", "PENDING_CASH", "FAILED"
    val orderStatus: String,   // "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"
    val idempotencyKey: String? = null,
    val createdAt: String
)
```

---

## 3. Retrofit Interface Definition

```kotlin
interface MediCartApiService {

    // Auth
    @POST("api/auth/request-otp")
    suspend fun requestOtp(@Body req: RequestOtpRequest): ApiResponse<RequestOtpResponseData>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body req: VerifyOtpRequest): ApiResponse<VerifyOtpResponseData>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): ApiResponse<UserDto>

    // Products
    @GET("api/products")
    suspend fun getProducts(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("inStockOnly") inStockOnly: Boolean? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<ProductListResponseData>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponse<ProductDto>

    @GET("api/categories")
    suspend fun getCategories(): ApiResponse<List<String>>

    // Addresses
    @GET("api/addresses")
    suspend fun getAddresses(): ApiResponse<List<AddressDto>>

    @POST("api/addresses")
    suspend fun createAddress(@Body address: AddressDto): ApiResponse<AddressDto>

    @PUT("api/addresses/{id}")
    suspend fun updateAddress(@Path("id") id: String, @Body address: AddressDto): ApiResponse<AddressDto>

    @DELETE("api/addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): ApiResponse<Unit>

    // Orders
    @POST("api/orders")
    suspend fun createOrder(@Body order: CreateOrderRequest): ApiResponse<OrderDto>

    @GET("api/orders")
    suspend fun getOrders(): ApiResponse<List<OrderDto>>

    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id", encoded = true) id: String): ApiResponse<OrderDto>
}
```
