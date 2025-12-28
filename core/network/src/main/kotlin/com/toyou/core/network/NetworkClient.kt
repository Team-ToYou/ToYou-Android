package com.toyou.core.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object NetworkClient {

    private const val BASE_URL = "https://to-you.shop"

    private var retrofit: Retrofit? = null
    private var authRetrofit: Retrofit? = null
    private var accessToken: String? = null

    fun setAccessToken(token: String) {
        accessToken = token
        // Reset auth retrofit to apply new token
        authRetrofit = null
    }

    fun getAccessToken(): String? = accessToken

    private fun provideAuthOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                accessToken?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }
                val request = requestBuilder.build()
                val response = chain.proceed(request)
                Timber.d("Response code: ${response.code}")
                response
            }
            .build()
    }

    /**
     * Non-authenticated Retrofit client
     */
    fun getClient(): Retrofit {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().also { retrofit = it }
    }

    /**
     * Authenticated Retrofit client with Bearer token
     */
    fun getAuthClient(): Retrofit {
        return authRetrofit ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideAuthOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().also { authRetrofit = it }
    }
}
