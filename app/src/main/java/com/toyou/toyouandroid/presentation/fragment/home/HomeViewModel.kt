package com.toyou.toyouandroid.presentation.fragment.home

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.home.repository.IHomeRepository
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.calendar.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: IHomeRepository,
    private val tokenManager: TokenManager
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
                emotionText = "멘트",
                diaryCards = null,
                yesterdayCards = emptyList()
            )
        }
    }

    private fun loadYesterdayCards() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val response = homeRepository.getYesterdayCard()
                Timber.tag("HomeViewModel").d("yesterdayCards: ${response.result}")
                if (response.isSuccess) {
                    updateState {
                        copy(
                            yesterdayCards = response.result.yesterday,
                            isLoading = false,
                            isEmpty = response.result.yesterday.isEmpty()
                        )
                    }
                    Timber.tag("HomeViewModel").d("yesterdayCards: ${response.result.yesterday}")
                } else {
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { loadYesterdayCards() },
                        onFailure = {
                            Timber.tag("HomeViewModel").d("refresh error")
                            updateState {
                                copy(
                                    isLoading = false,
                                    yesterdayCards = emptyList()
                                )
                            }
                            sendEvent(HomeEvent.TokenExpired)
                        }
                    )
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