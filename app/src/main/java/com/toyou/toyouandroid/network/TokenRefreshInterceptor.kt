package com.toyou.toyouandroid.network

import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class TokenRefreshInterceptor(private val tokenStorage: TokenStorage, private val authService: AuthService) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            // 1. Refresh the token
            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken != null) {
                val refreshResponse = authService.reissue(refreshToken).execute() // Synchronous call for token refresh
                if (refreshResponse.isSuccessful) {
//                    val newAccessToken = refreshResponse.body()?.accessToken // Assuming your response structure
//                    tokenStorage.saveTokens(newAccessToken!!, refreshToken) // Update stored tokens
//                    AuthNetworkModule.setAccessToken(newAccessToken)

                    refreshResponse.headers()["access_token"]?.let { newAccessToken ->
                        refreshResponse.headers()["refresh_token"]?.let { newRefreshToken ->
                            Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")

                            // 암호화된 토큰 저장소에 저장
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)

                            // 인증 네트워크 모듈에 access token 저장
                            AuthNetworkModule.setAccessToken(newAccessToken)

                            Timber.i("Tokens saved successfully")
                        } ?: Timber.e("Refresh token missing in refreshResponse headers")
                    } ?: Timber.e("Access token missing in refreshResponse headers")

                    val newAccessToken = refreshResponse.headers()["access_token"]
                    val newRefreshToken = refreshResponse.headers()["refresh_token"]
                    tokenStorage.saveTokens(newAccessToken!!, newRefreshToken!!)
                    AuthNetworkModule.setAccessToken(newAccessToken)

                    // 2. Retry the original request with the new token
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    // Handle refresh token failure (e.g., logout)
                    // ...
                }
            } else {
                // Handle missing refresh token (e.g., logout)
                // ...
            }
        }

        return response
    }
}