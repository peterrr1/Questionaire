package com.example.questionaire.utils.managers

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.questionaire.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
    Stores the JWT authentication token
 */
class TokenManager(private val context: Context) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("jwt_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("jwt_refresh_token")
    }

    fun getToken(type: String): Flow<String?> {
        val key = if (type == "ACCESS") {
            ACCESS_TOKEN
        } else{
            REFRESH_TOKEN
        }

        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }


    suspend fun saveToken(token: String, type: String) {
        val key = if (type == "ACCESS") {
            ACCESS_TOKEN
        } else{
            REFRESH_TOKEN
        }
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[key] = token
            }
        }
    }

    suspend fun deleteToken(type: String) {
        val key = if (type == "ACCESS") {
            ACCESS_TOKEN
        } else{
            REFRESH_TOKEN
        }
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences.remove(key)
            }
        }
    }
}