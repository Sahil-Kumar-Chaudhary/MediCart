package com.medicart.app.data.remote

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.medicart.app.BuildConfig
import com.medicart.app.data.local.SecureSessionManager
import com.medicart.app.data.remote.api.MediCartApiService
import com.medicart.app.data.remote.dto.ApiResponse
import com.medicart.app.data.remote.interceptor.AuthInterceptor
import com.medicart.app.data.remote.interceptor.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network Module providing configured OkHttp and Retrofit instances.
 */
object NetworkModule {

    private var customBaseUrl: String? = null
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun setCustomBaseUrl(url: String?) {
        customBaseUrl = if (url != null && !url.endsWith("/")) "$url/" else url
    }

    private fun isEmulator(): Boolean {
        return try {
            (android.os.Build.FINGERPRINT.startsWith("generic")
                    || android.os.Build.FINGERPRINT.startsWith("unknown")
                    || android.os.Build.MODEL.contains("google_sdk")
                    || android.os.Build.MODEL.contains("Emulator")
                    || android.os.Build.MODEL.contains("Android SDK built for x86")
                    || android.os.Build.MANUFACTURER.contains("Genymotion")
                    || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                    || "google_sdk" == android.os.Build.PRODUCT)
        } catch (_: Throwable) {
            false
        }
    }

    fun getBaseUrl(): String {
        if (customBaseUrl != null) return customBaseUrl!!
        val defaultUrl = BuildConfig.BASE_URL
        // On physical devices connected via ADB reverse, route 10.0.2.2 to 127.0.0.1 loopback
        return if (BuildConfig.DEBUG && !isEmulator() && defaultUrl.contains("10.0.2.2")) {
            defaultUrl.replace("10.0.2.2", "127.0.0.1")
        } else {
            defaultUrl
        }
    }

    fun createOkHttpClient(
        sessionManager: SecureSessionManager? = null,
        onUnauthorized: (() -> Unit)? = null
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(ErrorInterceptor(sessionManager, onUnauthorized))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun createRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String = getBaseUrl()
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun createApiService(
        sessionManager: SecureSessionManager? = null,
        baseUrl: String = getBaseUrl(),
        onUnauthorized: (() -> Unit)? = null
    ): MediCartApiService {
        val client = createOkHttpClient(sessionManager, onUnauthorized)
        val retrofit = createRetrofit(client, baseUrl)
        return retrofit.create(MediCartApiService::class.java)
    }

    /**
     * Helper to parse error bodies into structured error messages
     */
    fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBodyString = response.errorBody()?.string()
            if (!errorBodyString.isNullOrBlank()) {
                val envelope = gson.fromJson(errorBodyString, ApiResponse::class.java)
                envelope.message
            } else {
                "HTTP ${response.code()} Error"
            }
        } catch (_: Exception) {
            "HTTP ${response.code()}: ${response.message()}"
        }
    }
}
