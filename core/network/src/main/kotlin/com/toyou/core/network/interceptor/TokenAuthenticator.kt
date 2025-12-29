package com.toyou.core.network.interceptor

import com.toyou.core.common.AppConstants
import com.toyou.core.datastore.TokenStorage
import com.toyou.core.network.api.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authServiceProvider: Provider<AuthService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val retryCount = response.request.header(AppConstants.Network.HEADER_RETRY_COUNT)?.toIntOrNull() ?: 0

        if (retryCount >= AppConstants.Network.MAX_RETRY_COUNT) {
            Timber.e("Token refresh max retries reached (${AppConstants.Network.MAX_RETRY_COUNT})")
            return null
        }

        Timber.d("Received 401 - attempting token refresh (attempt ${retryCount + 1})")

        // OkHttp Authenticator는 동기식이므로 blocking call 사용
        // Dispatchers.IO를 사용하여 성능 최적화
        @Suppress("DEPRECATION")
        val refreshToken = tokenStorage.getRefreshTokenBlocking()
        if (refreshToken.isNullOrEmpty()) {
            Timber.e("No refresh token available")
            return null
        }

        return runBlocking(Dispatchers.IO) {
            try {
                val authService = authServiceProvider.get()
                val refreshResponse = authService.reissue(refreshToken)

                if (refreshResponse.isSuccessful) {
                    val newAccessToken = refreshResponse.headers()["access_token"]
                    val newRefreshToken = refreshResponse.headers()["refresh_token"]

                    if (newAccessToken != null && newRefreshToken != null) {
                        Timber.d("Token refresh successful")
                        tokenStorage.saveTokens(newAccessToken, newRefreshToken)

                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .header(AppConstants.Network.HEADER_RETRY_COUNT, (retryCount + 1).toString())
                            .build()
                    } else {
                        Timber.e("Token missing in refresh response headers")
                        null
                    }
                } else {
                    val errorMessage = refreshResponse.errorBody()?.string() ?: "Unknown error"
                    Timber.e("Token refresh failed: $errorMessage")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Token refresh exception")
                null
            }
        }
    }
}
