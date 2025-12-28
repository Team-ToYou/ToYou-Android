package com.toyou.toyouandroid.utils

import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.core.datastore.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) {
    suspend fun refreshTokenSuspend(): Result<String> {
        return try {
            val response = authService.reissue(tokenStorage.getRefreshToken().toString())
            if (response.isSuccessful) {
                val newAccessToken = response.headers()["access_token"]
                val newRefreshToken = response.headers()["refresh_token"]
                if (newAccessToken != null && newRefreshToken != null) {
                    Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")
                    tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                    AuthNetworkModule.setAccessToken(newAccessToken)
                    Timber.i("Tokens saved successfully")
                    Result.success(newAccessToken)
                } else {
                    Timber.e("Token missing in response headers")
                    Result.failure(Exception("Token missing in response headers"))
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Timber.e("API Error: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Timber.e("Token refresh failed: ${e.message}")
            Result.failure(e)
        }
    }

    fun refreshToken(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = refreshTokenSuspend()
            result.onSuccess { token ->
                onSuccess(token)
            }.onFailure {
                onFailure()
            }
        }
    }
}
