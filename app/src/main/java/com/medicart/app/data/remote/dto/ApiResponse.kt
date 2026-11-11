package com.medicart.app.data.remote.dto

/**
 * Standard uniform response envelope matching MediCart REST API
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errorCode: String? = null,
    val errors: List<Any>? = null
)
