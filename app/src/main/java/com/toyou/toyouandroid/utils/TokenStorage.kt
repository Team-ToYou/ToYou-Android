package com.toyou.toyouandroid.utils

import android.content.Context

class TokenStorage(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun saveFcmToken(fcmToken: String){
        sharedPreferences.edit().apply {
            putString("fcm_token", fcmToken)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)
    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)

    fun getFcmToken() : String? = sharedPreferences.getString("fcm_token", null)

    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove("access_token")
            remove("refresh_token")
            apply()
        }
    }

    companion object {
        private const val KEY_IS_TOKEN_SENT = "is_token_sent"
    }

    fun isTokenSent(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_TOKEN_SENT, false)
    }

    fun setTokenSent(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_TOKEN_SENT, value).apply()
    }

}