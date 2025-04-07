package com.toyou.toyouandroid.utils

import android.content.Context

class TutorialStorage(context: Context) {
    private val preferences = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TUTORIAL_SHOWN = "tutorial_shown"
    }

    fun isTutorialShown(): Boolean {
        return preferences.getBoolean(TUTORIAL_SHOWN, false)
    }

    fun setTutorialShown() {
        preferences.edit().putBoolean(TUTORIAL_SHOWN, true).apply()
    }

    fun setTutorialNotShown() {
        preferences.edit().putBoolean(TUTORIAL_SHOWN, false).apply()
    }
}