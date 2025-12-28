package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.create.repository.ICreateRepository
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val repository: ICreateRepository
) : MviViewModel<UserUiState, UserEvent, UserAction>(
    initialState = UserUiState()
) {

    private val _cardId = MutableLiveData<Int?>()
    val cardId: LiveData<Int?> get() = _cardId

    private val _emotion = MutableLiveData<String?>()
    val emotion: LiveData<String?> get() = _emotion

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _cardNum = MutableLiveData<Int>()
    val cardNum: LiveData<Int> get() = _cardNum

    private val _uncheckedAlarm = MutableLiveData<Boolean>()
    val uncheckedAlarm: LiveData<Boolean> get() = _uncheckedAlarm

    init {
        state.onEach { uiState ->
            _cardId.value = uiState.cardId
            _emotion.value = uiState.emotion
            _nickname.value = uiState.nickname
            _cardNum.value = uiState.cardNum
            _uncheckedAlarm.value = uiState.uncheckedAlarm
        }.launchIn(viewModelScope)
    }

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
                val response = repository.getHomeEntryData()
                if (response.isSuccess) {
                    updateState {
                        copy(
                            cardId = response.result.id,
                            emotion = response.result.emotion,
                            nickname = response.result.nickname,
                            cardNum = response.result.question,
                            uncheckedAlarm = response.result.alarm,
                            isLoading = false
                        )
                    }
                    Timber.tag("UserViewModel").d("API 성공, ${response.result}")
                } else {
                    Timber.tag("UserViewModel").d("API 실패: ${response.message}")
                    updateState { copy(isLoading = false) }
                    tokenManager.refreshToken(
                        onSuccess = { performLoadHomeEntry() },
                        onFailure = {
                            Timber.e("getHomeEntry API call failed")
                            sendEvent(UserEvent.TokenExpired)
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("UserViewModel").d("예외 발생: ${e.message}")
                updateState { copy(isLoading = false) }
                tokenManager.refreshToken(
                    onSuccess = { performLoadHomeEntry() },
                    onFailure = {
                        Timber.e("getHomeEntry API call failed")
                        sendEvent(UserEvent.ShowError(e.message ?: "Unknown error"))
                    }
                )
            }
        }
    }

    private fun performUpdateCardId(cardId: Int?) {
        updateState { copy(cardId = cardId) }
    }

    fun getHomeEntry() = onAction(UserAction.LoadHomeEntry)

    fun updateCardIdFromOtherViewModel(otherViewModel: CardViewModel) {
        otherViewModel.cardId.observeForever { newCardId ->
            onAction(UserAction.UpdateCardId(newCardId))
        }
    }
}
