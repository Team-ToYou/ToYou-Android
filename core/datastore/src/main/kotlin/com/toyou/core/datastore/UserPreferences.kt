package com.toyou.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.userDataStore

    val userIdFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_USER_ID] ?: -1
    }

    val nicknameFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_NICKNAME]
    }

    val statusFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_STATUS]
    }

    suspend fun setUserId(value: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = value
        }
    }

    suspend fun setNickname(value: String?) {
        dataStore.edit { prefs ->
            if (value != null) {
                prefs[KEY_NICKNAME] = value
            } else {
                prefs.remove(KEY_NICKNAME)
            }
        }
    }

    suspend fun setStatus(value: String?) {
        dataStore.edit { prefs ->
            if (value != null) {
                prefs[KEY_STATUS] = value
            } else {
                prefs.remove(KEY_STATUS)
            }
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    // Blocking versions for backward compatibility
    var userId: Int
        get() = runBlocking { dataStore.data.map { it[KEY_USER_ID] ?: -1 }.first() }
        set(value) = runBlocking { setUserId(value) }

    var nickname: String?
        get() = runBlocking { dataStore.data.map { it[KEY_NICKNAME] }.first() }
        set(value) = runBlocking { setNickname(value) }

    var status: String?
        get() = runBlocking { dataStore.data.map { it[KEY_STATUS] }.first() }
        set(value) = runBlocking { setStatus(value) }

    fun clearSync() = runBlocking { clear() }

    companion object {
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_NICKNAME = stringPreferencesKey("nickname")
        private val KEY_STATUS = stringPreferencesKey("status")
    }
}
