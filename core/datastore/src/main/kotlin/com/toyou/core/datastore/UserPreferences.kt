package com.toyou.core.datastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userId: Int
        get() = preferences.getInt(KEY_USER_ID, -1)
        set(value) = preferences.edit().putInt(KEY_USER_ID, value).apply()

    var nickname: String?
        get() = preferences.getString(KEY_NICKNAME, null)
        set(value) = preferences.edit().putString(KEY_NICKNAME, value).apply()

    var status: String?
        get() = preferences.getString(KEY_STATUS, null)
        set(value) = preferences.edit().putString(KEY_STATUS, value).apply()

    fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_STATUS = "status"
    }
}
