package com.toyou.toyouandroid.utils

import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.TestNetworkModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TokenManager(private val authService: AuthService, private val tokenStorage: TokenStorage) {

    var accessToken: String? = null
        private set // 외부에서 접근할 수 없도록 setter를 private으로 설정

    fun setAccessToken(token: String) {
        accessToken = token
    }

    fun refreshToken(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        authService.reissue(tokenStorage.getRefreshToken().toString()).enqueue(object :
            Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    response.headers()["access_token"]?.let { newAccessToken ->
                        response.headers()["refresh_token"]?.let { newRefreshToken ->
                            Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")

                            // 암호화된 토큰 저장소에 저장
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)

                            // 인증 네트워크 모듈에 access token 저장
                            AuthNetworkModule.setAccessToken(newAccessToken)
                            TestNetworkModule.setAccessToken(newAccessToken)

                            onSuccess(newAccessToken)

                            Timber.i("Tokens saved successfully")
                        } ?: Timber.e("Refresh token missing in response headers")
                    } ?: Timber.e("Access token missing in response headers")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                onFailure()
            }
        })
    }
}