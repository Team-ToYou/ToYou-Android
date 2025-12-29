package com.toyou.feature.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.AppConstants
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.common.utils.getCurrentDate
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.IHomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: IHomeRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<HomeUiState, HomeEvent, HomeAction>(
    initialState = HomeUiState(currentDate = getCurrentDate())
) {

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadYesterdayCards -> loadYesterdayCards()
            is HomeAction.UpdateEmotion -> updateEmotion(action.emotionText)
            is HomeAction.ResetState -> resetState()
        }
    }

    private fun updateEmotion(emotionText: String) {
        updateState { copy(emotionText = emotionText) }
    }

    private fun resetState() {
        updateState {
            copy(
                emotionText = AppConstants.Ui.DEFAULT_EMOTION_TEXT,
                yesterdayCards = emptyList()
            )
        }
    }

    private fun loadYesterdayCards() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                when (val result = homeRepository.getYesterdayCard()) {
                    is DomainResult.Success -> {
                        val cards = result.data.cards
                        Timber.tag("HomeViewModel").d("yesterdayCards: $cards")
                        updateState {
                            copy(
                                yesterdayCards = cards,
                                isLoading = false,
                                isEmpty = cards.isEmpty()
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        updateState { copy(isLoading = false) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { loadYesterdayCards() },
                            onFailure = {
                                updateState {
                                    copy(
                                        isLoading = false,
                                        yesterdayCards = emptyList()
                                    )
                                }
                                sendEvent(HomeEvent.TokenExpired)
                            },
                            tag = "HomeViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("HomeViewModel").e(e, "Error getting yesterday cards")
                updateState {
                    copy(
                        yesterdayCards = emptyList(),
                        isLoading = false,
                        isEmpty = true
                    )
                }
                sendEvent(HomeEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }
}
