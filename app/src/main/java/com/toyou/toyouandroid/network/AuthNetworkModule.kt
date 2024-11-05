package com.toyou.toyouandroid.network

import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthNetworkModule {

    private const val BASE_URL = "https://to-you.store"

    private var retrofit: Retrofit? = null

    private var accessToken: String? = null

    fun setAccessToken(token: String) {
        accessToken = token
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                accessToken?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    private fun createOkHttpClient(tokenStorage: TokenStorage, authService: AuthService): OkHttpClient {
        val builder = OkHttpClient.Builder()
        // ... (other interceptors)
        builder.addInterceptor(TokenRefreshInterceptor(tokenStorage, authService)) // Add refresh interceptor
        return builder.build()
    }

    fun getClient(): Retrofit {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().also { retrofit = it }
    }
}