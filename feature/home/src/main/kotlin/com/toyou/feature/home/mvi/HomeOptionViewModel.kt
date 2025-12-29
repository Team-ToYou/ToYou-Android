package com.toyou.feature.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.network.model.emotion.EmotionRequest
import com.toyou.core.network.api.EmotionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeOptionViewModel @Inject constructor(
    private val emotionService: EmotionService,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<HomeOptionUiState, HomeOptionEvent, HomeOptionAction>(
    initialState = HomeOptionUiState()
) {

    override fun handleAction(action: HomeOptionAction) {
        when (action) {
            is HomeOptionAction.UpdateEmotion -> performUpdateEmotion(action.emotionRequest)
        }
    }

    private fun performUpdateEmotion(emotionRequest: EmotionRequest) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            try {
                val response = emotionService.patchEmotion(emotionRequest)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        updateState { copy(emotionResponse = body, isLoading = false) }
                    }
                    Timber.tag("emotionResponse").d("emotionResponse: $response")
                } else {
                    Timber.tag("API Error").e("Failed to update emotion. Code: ${response.code()}, Message: ${response.message()}")
                    if (errorHandler.handleUnauthorized(
                        responseCode = response.code(),
                        onRetry = { performUpdateEmotion(emotionRequest) },
                        onFailure = {
                            updateState { copy(isLoading = false) }
                            sendEvent(HomeOptionEvent.TokenExpired)
                        },
                        tag = "HomeOptionViewModel"
                    )) {
                        return@launch
                    }
                    updateState { copy(isLoading = false) }
                    sendEvent(HomeOptionEvent.ShowError("감정 업데이트에 실패했습니다."))
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error occurred during API call")
                updateState { copy(isLoading = false) }
                sendEvent(HomeOptionEvent.ShowError("네트워크 오류가 발생했습니다."))
            }
        }
    }

    fun updateEmotion(emotionRequest: EmotionRequest) = onAction(HomeOptionAction.UpdateEmotion(emotionRequest))
}
