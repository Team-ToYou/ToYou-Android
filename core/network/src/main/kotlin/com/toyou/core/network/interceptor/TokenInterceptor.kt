package com.toyou.core.network.interceptor

import com.toyou.core.datastore.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // OkHttp Interceptor는 동기식이므로 blocking call 사용
        // Dispatchers.IO를 사용하여 성능 최적화
        @Suppress("DEPRECATION")
        val accessToken = tokenStorage.getAccessTokenBlocking()

        val requestBuilder = originalRequest.newBuilder()
        if (!accessToken.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
            Timber.d("Added Authorization header")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)
        Timber.d("Response code: ${response.code}")
        return response
    }
}
