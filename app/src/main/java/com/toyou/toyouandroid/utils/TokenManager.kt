package com.toyou.toyouandroid.utils

import com.toyou.core.datastore.TokenStorage
import com.toyou.core.network.api.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.toyou.core.datastore.TokenManager as ITokenManager

@Singleton
class TokenManagerImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : ITokenManager {
    override suspend fun refreshTokenSuspend(): Result<String> {
        return try {
            // Flow를 사용하여 비동기적으로 토큰 가져오기
            val refreshToken = tokenStorage.refreshTokenFlow.first()
            if (refreshToken.isNullOrEmpty()) {
                Timber.e("No refresh token available")
                return Result.failure(Exception("No refresh token available"))
            }

            val response = authService.reissue(refreshToken)
            if (response.isSuccessful) {
                val newAccessToken = response.headers()["access_token"]
                val newRefreshToken = response.headers()["refresh_token"]
                if (newAccessToken != null && newRefreshToken != null) {
                    Timber.d("Tokens received from server")
                    tokenStorage.saveTokens(newAccessToken, newRefreshToken)
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

    override fun refreshToken(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
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
