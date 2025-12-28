package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.emotion.dto.EmotionResponse
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeOptionViewModel @Inject constructor(
    private val emotionService: EmotionService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _emotionResponse = MutableLiveData<EmotionResponse>()
    val emotionResponse: LiveData<EmotionResponse> get() = _emotionResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun updateEmotion(emotionRequest: EmotionRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = emotionService.patchEmotionSuspend(emotionRequest)
                if (response.isSuccessful) {
                    _emotionResponse.postValue(response.body())
                    Timber.tag("emotionResponse").d("emotionResponse: $response")
                } else {
                    Timber.tag("API Error").e("Failed to update emotion. Code: ${response.code()}, Message: ${response.message()}")
                    if (response.code() == 401) {
                        tokenManager.refreshToken(
                            onSuccess = { updateEmotion(emotionRequest) },
                            onFailure = { 
                                Timber.e("Failed to refresh token and update emotion")
                                _errorMessage.value = "인증 실패. 다시 로그인해주세요."
                            }
                        )
                    } else {
                        _errorMessage.value = "감정 업데이트에 실패했습니다."
                    }
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error occurred during API call")
                _errorMessage.value = "네트워크 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }
}