package com.toyou.toyouandroid.utils

import android.content.Context
import com.toyou.toyouandroid.fcm.dto.request.Token

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
}