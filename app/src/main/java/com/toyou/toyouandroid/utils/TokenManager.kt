package com.toyou.toyouandroid.utils

import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.network.AuthNetworkModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TokenManager(private val authService: AuthService, private val tokenStorage: TokenStorage) {

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
                            Timber.i("Tokens saved successfully")

                            // 인증 네트워크 모듈에 재발급받은 access token 저장
                            AuthNetworkModule.setAccessToken(newAccessToken)

                            // 성공 콜백 실행
                            onSuccess(newAccessToken)

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