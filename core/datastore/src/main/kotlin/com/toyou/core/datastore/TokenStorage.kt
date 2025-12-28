package com.toyou.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
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

    suspend fun isTokenSentSuspend(): Boolean {
        return dataStore.data.map { prefs ->
            prefs[KEY_IS_TOKEN_SENT] ?: false
        }.first()
    }

    // Blocking versions for backward compatibility during migration
    fun getAccessToken(): String? = runBlocking {
        dataStore.data.map { it[KEY_ACCESS_TOKEN] }.first()
    }

    fun getRefreshToken(): String? = runBlocking {
        dataStore.data.map { it[KEY_REFRESH_TOKEN] }.first()
    }

    fun getFcmToken(): String? = runBlocking {
        dataStore.data.map { it[KEY_FCM_TOKEN] }.first()
    }

    fun isTokenSent(): Boolean = runBlocking {
        dataStore.data.map { it[KEY_IS_TOKEN_SENT] ?: false }.first()
    }

    // Blocking write methods for backward compatibility
    fun saveTokensSync(accessToken: String, refreshToken: String) = runBlocking {
        saveTokens(accessToken, refreshToken)
    }

    fun saveFcmTokenSync(fcmToken: String) = runBlocking {
        saveFcmToken(fcmToken)
    }

    fun clearTokensSync() = runBlocking {
        clearTokens()
    }

    fun setTokenSentSync(value: Boolean) = runBlocking {
        setTokenSent(value)
    }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_IS_TOKEN_SENT = booleanPreferencesKey("is_token_sent")
    }
}
