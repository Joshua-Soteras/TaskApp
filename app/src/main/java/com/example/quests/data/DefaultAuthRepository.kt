package com.example.quests.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.quests.data.source.network.ApiClient
import com.example.quests.data.source.network.model.QuestsResponse
import com.example.quests.di.DefaultDispatcher
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Mostly taken from
 * https://github.com/android/codelab-android-datastore/blob/preferences_datastore/app/src/main/java/com/codelab/android/datastore/ui/TasksViewModel.kt
 */
class DefaultAuthRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val dataStore: DataStore<Preferences>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : AuthRepository {

    private val TAG: String = "DefaultAuthRepo"

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    /**
     * Get auth token flow
     */
    override val authTokenFlow: Flow<AuthToken> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Timber.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapAuthToken(preferences)
        }

    /**
     * Log in with [username] and [password]. Updates access token and refresh token
     * if login is successful.
     */
    override suspend fun login(
        username: String,
        password: String,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val response = apiClient.login(username, password)
        response.suspendOnSuccess {
            data?.accessToken?.let { updateAccessToken(it) }
            data?.refreshToken?.let { updateRefreshToken(it) }
            onComplete();
        }.onError {
            val e: QuestsResponse? = this.deserializeErrorBody<String, QuestsResponse>()
            onError(e?.error?.detail)
        }.onException {
            onError(message)
        }
    }

    override suspend fun register(username: String, password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }

    override suspend fun clearAuthToken() {
        dataStore.edit { it.clear() }
    }

    suspend fun updateAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun updateRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    private fun mapAuthToken(preferences: Preferences): AuthToken =
        AuthToken(
            accessToken = preferences[ACCESS_TOKEN] ?: "",
            refreshToken = preferences[REFRESH_TOKEN] ?: ""
        )
}