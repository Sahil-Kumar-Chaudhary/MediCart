package com.medicart.app.data.remote.dto

data class ProductDto(
    val _id: String,
    val productId: String,
    val name: String,
    val genericName: String,
    val description: String? = null,
    val category: String,
    val pricePaise: Long,
    val mrpPaise: Long,
    val packSize: String,
    val stockCount: Int,
    val isAvailable: Boolean,
    val isExpired: Boolean,
    val expiryDate: String,
    val imageUrl: String? = null
)

data class PaginationDto(
    val totalItems: Int,
    val currentPage: Int,
    val totalPages: Int,
    val limit: Int
)

data class ProductListResponseData(
    val products: List<ProductDto>,
    val pagination: PaginationDto? = null
)
