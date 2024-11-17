package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.emotion.dto.EmotionResponse
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class HomeOptionViewModel(private val tokenManager: TokenManager) : ViewModel() {

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
                    tokenManager.refreshToken(
                        onSuccess = { updateEmotion(emotionRequest) }, // 토큰 갱신 후 다시 요청
                        onFailure = { Timber.e("Failed to refresh token and get notices") }
                    )
                    Timber.tag("API Error").e("Failed to update emotion. Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EmotionResponse>, t: Throwable) {
                Timber.tag("API Failure").e(t, "Error occurred during API call")
            }
        })
    }
}
