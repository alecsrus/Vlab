package com.virtuallabs.premium

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "vlab_prefs")

class PremiumManager(private val context: Context) {

    private val KEY_PREMIUM = booleanPreferencesKey("is_premium")

    val isPremiumFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_PREMIUM] ?: false }

    suspend fun setPremium(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PREMIUM] = enabled
        }
    }

    suspend fun togglePremium() {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_PREMIUM] ?: false
            prefs[KEY_PREMIUM] = !current
        }
    }
}
