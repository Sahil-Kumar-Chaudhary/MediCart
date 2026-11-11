package com.medicart.app.data.remote.api

import com.medicart.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MediCartApiService {

    // Health
    @GET("health")
    suspend fun getHealth(): Response<Map<String, Any>>

    // Authentication
    @POST("api/auth/request-otp")
    suspend fun requestOtp(
        @Body request: RequestOtpRequest
    ): Response<ApiResponse<RequestOtpResponseData>>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<ApiResponse<VerifyOtpResponseData>>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

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
    ): Response<ApiResponse<ProductListResponseData>>

    @GET("api/products/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): Response<ApiResponse<ProductDto>>

    @GET("api/categories")
    suspend fun getCategories(): Response<ApiResponse<List<String>>>

    // Addresses
    @GET("api/addresses")
    suspend fun getAddresses(): Response<ApiResponse<List<AddressDto>>>

    @POST("api/addresses")
    suspend fun createAddress(
        @Body address: AddressDto
    ): Response<ApiResponse<AddressDto>>

    @PUT("api/addresses/{id}")
    suspend fun updateAddress(
        @Path("id") id: String,
        @Body address: AddressDto
    ): Response<ApiResponse<AddressDto>>

    @DELETE("api/addresses/{id}")
    suspend fun deleteAddress(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // Orders
    @POST("api/orders")
    suspend fun createOrder(
        @Body order: CreateOrderRequest
    ): Response<ApiResponse<OrderDto>>

    @GET("api/orders")
    suspend fun getOrders(): Response<ApiResponse<List<OrderDto>>>

    @GET("api/orders/{id}")
    suspend fun getOrderById(
        @Path(value = "id", encoded = true) id: String
    ): Response<ApiResponse<OrderDto>>
}
