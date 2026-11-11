package com.medicart.app.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Secure session manager utilizing Jetpack Security EncryptedSharedPreferences
 * with automatic fallback to standard SharedPreferences for test environments.
 */
class SecureSessionManager(context: Context) {

    private val preferences: SharedPreferences = try {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            PREFS_FILE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Throwable) {
        Log.w(TAG, "EncryptedSharedPreferences unavailable, falling back to standard preferences", e)
        context.getSharedPreferences(FALLBACK_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(mobileNumber: String, token: String, recipientName: String = "Sahil Kumar") {
        preferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_MOBILE_NUMBER, mobileNumber)
            .putString(KEY_USER_NAME, recipientName)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun clearSession() {
        preferences.edit()
            .clear()
            .apply()
    }

    val isAuthenticated: Boolean
        get() = preferences.getBoolean(KEY_IS_LOGGED_IN, false) &&
                !preferences.getString(KEY_AUTH_TOKEN, null).isNullOrBlank()

    val mobileNumber: String
        get() = preferences.getString(KEY_MOBILE_NUMBER, "+91 98765 43210") ?: "+91 98765 43210"

    val userName: String
        get() = preferences.getString(KEY_USER_NAME, "Sahil Kumar") ?: "Sahil Kumar"

    val authToken: String?
        get() = preferences.getString(KEY_AUTH_TOKEN, null)

    companion object {
        private const val TAG = "SecureSessionManager"
        private const val PREFS_FILE_NAME = "medicart_secure_session_prefs"
        private const val FALLBACK_PREFS_NAME = "medicart_session_prefs"

        private const val KEY_AUTH_TOKEN = "key_auth_token"
        private const val KEY_MOBILE_NUMBER = "key_mobile_number"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_LOGIN_TIMESTAMP = "key_login_timestamp"
    }
}
