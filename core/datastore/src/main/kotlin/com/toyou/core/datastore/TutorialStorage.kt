package com.toyou.core.datastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TutorialStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)

    fun isTutorialShown(): Boolean {
        return preferences.getBoolean(KEY_TUTORIAL_SHOWN, false)
    }

    fun setTutorialShown() {
        preferences.edit().putBoolean(KEY_TUTORIAL_SHOWN, true).apply()
    }

    fun setTutorialNotShown() {
        preferences.edit().putBoolean(KEY_TUTORIAL_SHOWN, false).apply()
    }

    companion object {
        private const val KEY_TUTORIAL_SHOWN = "tutorial_shown"
    }
}
