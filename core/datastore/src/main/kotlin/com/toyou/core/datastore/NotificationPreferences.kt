package com.toyou.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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

    /**
     * FCM 서비스를 위한 동기식 구독 상태 접근 메서드
     * 주의: 이 메서드는 IO 작업을 차단하므로 FirebaseMessagingService에서만 사용해야 합니다.
     */
    @Deprecated(
        message = "Only use this in FirebaseMessagingService. Use isSubscribedFlow or suspend functions elsewhere.",
        replaceWith = ReplaceWith("isSubscribedFlow.first()"),
        level = DeprecationLevel.WARNING
    )
    fun isSubscribedBlocking(): Boolean = runBlocking(Dispatchers.IO) {
        isSubscribedFlow.first()
    }

    /**
     * FCM 서비스를 위한 동기식 구독 설정 메서드
     * 주의: 이 메서드는 IO 작업을 차단하므로 FirebaseMessagingService에서만 사용해야 합니다.
     */
    @Deprecated(
        message = "Only use this in FirebaseMessagingService. Use suspend setSubscribed() elsewhere.",
        replaceWith = ReplaceWith("setSubscribed(value)"),
        level = DeprecationLevel.WARNING
    )
    fun setSubscribedBlocking(value: Boolean) = runBlocking(Dispatchers.IO) {
        setSubscribed(value)
    }

    companion object {
        private val KEY_IS_SUBSCRIBED = booleanPreferencesKey("isSubscribed")
    }
}
