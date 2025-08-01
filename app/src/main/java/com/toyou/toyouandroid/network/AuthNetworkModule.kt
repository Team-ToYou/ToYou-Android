package com.toyou.toyouandroid.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object AuthNetworkModule {

    private const val BASE_URL = "https://to-you.shop"
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
                val response = chain.proceed(request)
                Timber.d("Response code: ${response.code}")
                response
            }
            .build()
    }

    fun getClient(): Retrofit {
        return retrofit ?: Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().also { retrofit = it }
    }
}