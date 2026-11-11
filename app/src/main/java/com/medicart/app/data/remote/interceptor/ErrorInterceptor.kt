package com.medicart.app.data.remote.interceptor

import android.util.Log
import com.medicart.app.data.local.SecureSessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Handles HTTP error status codes and triggers session invalidation on 401 Unauthorized.
 */
class ErrorInterceptor(
    private val sessionManager: SecureSessionManager?,
    private val onUnauthorized: (() -> Unit)? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            try {
                Log.w(TAG, "401 Unauthorized received - clearing invalid/expired user session")
            } catch (_: Throwable) {
                // Ignore missing Android Log mock in pure JVM unit tests
            }
            sessionManager?.clearSession()
            onUnauthorized?.invoke()
        }

        return response
    }

    companion object {
        private const val TAG = "ErrorInterceptor"
    }
}
