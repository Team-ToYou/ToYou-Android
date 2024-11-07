package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.emotion.dto.EmotionResponse
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.data.onboarding.dto.response.SignUpResponse
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class HomeOptionViewModel(private val authService: AuthService, private val tokenStorage: TokenStorage) : ViewModel() {

    private val _emotionResponse = MutableLiveData<EmotionResponse>()
    val emotionResponse: LiveData<EmotionResponse> get() = _emotionResponse

    private val apiService: EmotionService = AuthNetworkModule.getClient().create(EmotionService::class.java)

    fun updateEmotion(emotionRequest: EmotionRequest) {
        val call = apiService.patchEmotion(emotionRequest)

        call.enqueue(object : Callback<EmotionResponse> {
            override fun onResponse(
                call: Call<EmotionResponse>,
                response: Response<EmotionResponse>
            ) {
                if (response.isSuccessful) {
                    _emotionResponse.postValue(response.body())
                    Timber.tag("emotionResponse").d("emotionResponse: $response")
                } else {
                    Timber.tag("API Error").e("Failed to update emotion. Code: ${response.code()}, Message: ${response.message()}")
                    refreshAccessToken(emotionRequest)
                }
            }

            override fun onFailure(call: Call<EmotionResponse>, t: Throwable) {
                Timber.tag("API Failure").e(t, "Error occurred during API call")
            }
        })
    }

    private fun refreshAccessToken(emotionRequest: EmotionRequest) {
        authService.reissue(tokenStorage.getRefreshToken().toString()).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                if (response.isSuccessful) {
                    response.headers()["access_token"]?.let { newAccessToken ->
                        response.headers()["refresh_token"]?.let { newRefreshToken ->
                            Timber.d("Tokens received from server - Access: $newAccessToken, Refresh: $newRefreshToken")

                            // 암호화된 토큰 저장소에 저장
                            tokenStorage.saveTokens(newAccessToken, newRefreshToken)

                            // 인증 네트워크 모듈에 access token 저장
                            AuthNetworkModule.setAccessToken(newAccessToken)

                            updateEmotion(emotionRequest) // 원래의 요청을 재시도

                            Timber.i("Tokens saved successfully")
                        } ?: Timber.e("Refresh token missing in response headers")
                    } ?: Timber.e("Access token missing in response headers")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Timber.e("API Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Timber.e(t, "Error occurred during token refresh")
            }
        })
    }
}
