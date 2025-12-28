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

private val Context.tutorialDataStore: DataStore<Preferences> by preferencesDataStore(name = "tutorial_prefs")

@Singleton
class TutorialStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.tutorialDataStore

    val tutorialShownFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_TUTORIAL_SHOWN] ?: false
    }

    suspend fun setTutorialShown() {
        dataStore.edit { prefs ->
            prefs[KEY_TUTORIAL_SHOWN] = true
        }
    }

    suspend fun setTutorialNotShown() {
        dataStore.edit { prefs ->
            prefs[KEY_TUTORIAL_SHOWN] = false
        }
    }

    // Blocking versions for backward compatibility
    fun isTutorialShown(): Boolean = runBlocking {
        dataStore.data.map { it[KEY_TUTORIAL_SHOWN] ?: false }.first()
    }

    fun setTutorialShownSync() = runBlocking {
        setTutorialShown()
    }

    fun setTutorialNotShownSync() = runBlocking {
        setTutorialNotShown()
    }

    companion object {
        private val KEY_TUTORIAL_SHOWN = booleanPreferencesKey("tutorial_shown")
    }
}
