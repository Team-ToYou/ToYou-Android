package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.emotion.dto.EmotionResponse
import com.toyou.toyouandroid.data.emotion.service.EmotionService
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeOptionViewModel @Inject constructor(
    private val emotionService: EmotionService,
    private val tokenManager: TokenManager
) : MviViewModel<HomeOptionUiState, HomeOptionEvent, HomeOptionAction>(
    initialState = HomeOptionUiState()
) {

    private val _emotionResponse = MutableLiveData<EmotionResponse>()
    val emotionResponse: LiveData<EmotionResponse> get() = _emotionResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        state.onEach { uiState ->
            uiState.emotionResponse?.let { _emotionResponse.value = it }
            _isLoading.value = uiState.isLoading
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: HomeOptionAction) {
        when (action) {
            is HomeOptionAction.UpdateEmotion -> performUpdateEmotion(action.emotionRequest)
        }
    }

    private fun performUpdateEmotion(emotionRequest: EmotionRequest) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            _errorMessage.value = null

            try {
                val response = emotionService.patchEmotion(emotionRequest)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        updateState { copy(emotionResponse = body, isLoading = false) }
                    }
                    Timber.tag("emotionResponse").d("emotionResponse: $response")
                } else {
                    Timber.tag("API Error").e("Failed to update emotion. Code: ${response.code()}, Message: ${response.message()}")
                    if (response.code() == 401) {
                        tokenManager.refreshToken(
                            onSuccess = { performUpdateEmotion(emotionRequest) },
                            onFailure = {
                                Timber.e("Failed to refresh token and update emotion")
                                updateState { copy(isLoading = false) }
                                _errorMessage.value = "인증 실패. 다시 로그인해주세요."
                                sendEvent(HomeOptionEvent.TokenExpired)
                            }
                        )
                    } else {
                        updateState { copy(isLoading = false) }
                        _errorMessage.value = "감정 업데이트에 실패했습니다."
                        sendEvent(HomeOptionEvent.ShowError("감정 업데이트에 실패했습니다."))
                    }
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error occurred during API call")
                updateState { copy(isLoading = false) }
                _errorMessage.value = "네트워크 오류가 발생했습니다."
                sendEvent(HomeOptionEvent.ShowError("네트워크 오류가 발생했습니다."))
            }
        }
    }

    fun updateEmotion(emotionRequest: EmotionRequest) = onAction(HomeOptionAction.UpdateEmotion(emotionRequest))
}
