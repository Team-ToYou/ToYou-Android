package com.toyou.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "fcm_preferences")

@Singleton
class NotificationPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.notificationDataStore

    val isSubscribedFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_IS_SUBSCRIBED] ?: true
    }

    suspend fun setSubscribed(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_SUBSCRIBED] = value
        }
    }

    // Blocking versions for backward compatibility
    fun isSubscribed(): Boolean = runBlocking {
        dataStore.data.map { it[KEY_IS_SUBSCRIBED] ?: true }.first()
    }

    fun setSubscribedSync(value: Boolean) = runBlocking {
        setSubscribed(value)
    }

    companion object {
        private val KEY_IS_SUBSCRIBED = booleanPreferencesKey("isSubscribed")
    }
}
