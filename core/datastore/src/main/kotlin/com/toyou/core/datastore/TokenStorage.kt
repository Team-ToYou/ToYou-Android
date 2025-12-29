package com.toyou.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.tokenDataStore

    val accessTokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    val refreshTokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_REFRESH_TOKEN]
    }

    val fcmTokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_FCM_TOKEN]
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveFcmToken(fcmToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_FCM_TOKEN] = fcmToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
    }

    suspend fun setTokenSent(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_TOKEN_SENT] = value
        }
    }

    suspend fun isTokenSent(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[KEY_IS_TOKEN_SENT] ?: false
        }.first()
    }

    /**
     * OkHttp Interceptor/Authenticator를 위한 동기식 토큰 접근 메서드
     * 주의: 이 메서드는 IO 작업을 차단하므로 Interceptor/Authenticator에서만 사용해야 합니다.
     * 다른 곳에서는 반드시 Flow 또는 suspend 함수를 사용하세요.
     */
    @Deprecated(
        message = "Only use this in OkHttp Interceptor/Authenticator. Use accessTokenFlow or suspend functions elsewhere.",
        replaceWith = ReplaceWith("accessTokenFlow.first()"),
        level = DeprecationLevel.WARNING
    )
    fun getAccessTokenBlocking(): String? = runBlocking(Dispatchers.IO) {
        accessTokenFlow.first()
    }

    /**
     * OkHttp Authenticator를 위한 동기식 리프레시 토큰 접근 메서드
     * 주의: 이 메서드는 IO 작업을 차단하므로 Authenticator에서만 사용해야 합니다.
     */
    @Deprecated(
        message = "Only use this in OkHttp Authenticator. Use refreshTokenFlow or suspend functions elsewhere.",
        replaceWith = ReplaceWith("refreshTokenFlow.first()"),
        level = DeprecationLevel.WARNING
    )
    fun getRefreshTokenBlocking(): String? = runBlocking(Dispatchers.IO) {
        refreshTokenFlow.first()
    }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_IS_TOKEN_SENT = booleanPreferencesKey("is_token_sent")
    }
}
