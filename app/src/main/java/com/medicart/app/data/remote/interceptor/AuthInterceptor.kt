package com.medicart.app.data.remote.interceptor

import com.medicart.app.data.local.SecureSessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that attaches the active JWT Bearer token to authenticated requests.
 */
class AuthInterceptor(
    private val sessionManager: SecureSessionManager?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager?.authToken

        return if (!token.isNullOrBlank()) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            val requestWithAccept = originalRequest.newBuilder()
                .header("Accept", "application/json")
                .build()
            chain.proceed(requestWithAccept)
        }
    }
}
