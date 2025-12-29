package com.toyou.feature.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.ICreateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val errorHandler: ApiErrorHandler,
    private val repository: ICreateRepository
) : MviViewModel<UserUiState, UserEvent, UserAction>(
    initialState = UserUiState()
) {

    override fun handleAction(action: UserAction) {
        when (action) {
            is UserAction.LoadHomeEntry -> performLoadHomeEntry()
            is UserAction.UpdateCardId -> performUpdateCardId(action.cardId)
        }
    }

    private fun performLoadHomeEntry() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                when (val result = repository.getHomeEntryData()) {
                    is DomainResult.Success -> {
                        val data = result.data
                        updateState {
                            copy(
                                cardId = data.cardId,
                                emotion = data.emotion,
                                nickname = data.nickname,
                                cardNum = data.questionCount,
                                uncheckedAlarm = data.hasUncheckedAlarm,
                                isLoading = false
                            )
                        }
                        Timber.tag("UserViewModel").d("API 성공, $data")
                    }
                    is DomainResult.Error -> {
                        updateState { copy(isLoading = false) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performLoadHomeEntry() },
                            onFailure = {
                                sendEvent(UserEvent.TokenExpired)
                            },
                            tag = "UserViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("UserViewModel").d("예외 발생: ${e.message}")
                updateState { copy(isLoading = false) }
                sendEvent(UserEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun performUpdateCardId(cardId: Int?) {
        updateState { copy(cardId = cardId) }
    }

    fun getHomeEntry() = onAction(UserAction.LoadHomeEntry)

    fun updateCardId(cardId: Int?) = onAction(UserAction.UpdateCardId(cardId))
}
