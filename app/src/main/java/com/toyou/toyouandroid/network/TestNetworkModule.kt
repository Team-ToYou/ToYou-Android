package com.toyou.toyouandroid.network

import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object TestNetworkModule {

    private const val BASE_URL = "https://to-you.store"
    private var retrofit: Retrofit? = null
    private var accessToken: String? = null

    fun setAccessToken(token: String) {
        accessToken = token
    }

    private fun provideOkHttpClient(tokenManager: TokenManager, tokenStorage: TokenStorage): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                accessToken?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }
                val request = requestBuilder.build()
                val response = chain.proceed(request)

                if (response.code == 401) { // 401 Unauthorized
                    synchronized(this) {
                        // 토큰 재발급 시도
                        tokenManager.refreshToken(
                            onSuccess = { newAccessToken ->
                                // 새로운 토큰으로 다시 요청
                                tokenManager.setAccessToken(newAccessToken)

                                // 새로운 리프레시 토큰도 저장
                                val newRefreshToken = response.headers["refresh_token"]
                                if (newRefreshToken != null) {
                                    tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                                }

                                val newRequest = original.newBuilder()
                                    .header("Authorization", "Bearer $newAccessToken")
                                    .build()

                                chain.proceed(newRequest)
                            },
                            onFailure = {
                                // 재발급 실패 시 처리
                                Timber.e("Failed to refresh token")
                            }
                        )
                    }
                }

                response
            }
            .build()
    }

    fun getClient(tokenManager: TokenManager, tokenStorage: TokenStorage): Retrofit {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(tokenManager, tokenStorage))
            .addConverterFactory(GsonConverterFactory.create())
            .build().also { retrofit = it }
    }
}