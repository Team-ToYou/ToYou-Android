package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.profile.repository.IProfileRepository
import com.toyou.toyouandroid.presentation.fragment.mypage.DuplicateCheckMessageType
import com.toyou.toyouandroid.presentation.fragment.mypage.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignupNicknameViewModel @Inject constructor(
    private val profileRepository: IProfileRepository
) : MviViewModel<SignupNicknameUiState, SignupNicknameEvent, SignupNicknameAction>(
    initialState = SignupNicknameUiState()
) {

    private val _uiState = MutableLiveData(ProfileUiState())
    val uiStateLegacy: LiveData<ProfileUiState> get() = _uiState

    val nickname: LiveData<String> = MutableLiveData()
    val textCount: LiveData<String> = MutableLiveData("0/15")
    val duplicateCheckButtonTextColor: LiveData<Int> = MutableLiveData(0xFFA6A6A6.toInt())
    val duplicateCheckButtonBackground: LiveData<Int> = MutableLiveData(com.toyou.toyouandroid.R.drawable.next_button)
    val duplicateCheckMessage: LiveData<String> = MutableLiveData("중복된 닉네임인지 확인해주세요")
    val duplicateCheckMessageColor: LiveData<Int> = MutableLiveData(0xFF000000.toInt())
    val nextButtonBackground: LiveData<Int> = MutableLiveData(com.toyou.toyouandroid.R.drawable.next_button)
    val nextButtonTextColor: LiveData<Int> = MutableLiveData(0xFFA6A6A6.toInt())
    val isNextButtonEnabled: LiveData<Boolean> = MutableLiveData(false)

    private val _duplicateCheckMessageType = MutableLiveData<DuplicateCheckMessageType>()
    val duplicateCheckMessageType: LiveData<DuplicateCheckMessageType> get() = _duplicateCheckMessageType

    init {
        state.onEach { mviState ->
            _uiState.value = ProfileUiState(
                title = mviState.title,
                textCount = mviState.textCount,
                nickname = mviState.nickname,
                isDuplicateCheckEnabled = mviState.isDuplicateCheckEnabled,
                isNextButtonEnabled = mviState.isNextButtonEnabled,
                duplicateCheckMessage = mviState.duplicateCheckMessage,
                isNicknameValid = mviState.isNicknameValid
            )
            (nickname as MutableLiveData).value = mviState.nickname
            (textCount as MutableLiveData).value = mviState.textCount
            (duplicateCheckButtonTextColor as MutableLiveData).value = mviState.duplicateCheckButtonTextColor
            (duplicateCheckButtonBackground as MutableLiveData).value = mviState.duplicateCheckButtonBackground
            (duplicateCheckMessage as MutableLiveData).value = mviState.duplicateCheckMessage
            (duplicateCheckMessageColor as MutableLiveData).value = mviState.duplicateCheckMessageColor
            (nextButtonBackground as MutableLiveData).value = mviState.nextButtonBackground
            (nextButtonTextColor as MutableLiveData).value = mviState.nextButtonTextColor
            (isNextButtonEnabled as MutableLiveData).value = mviState.isNextButtonEnabled
            _duplicateCheckMessageType.value = mviState.duplicateCheckMessageType
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: SignupNicknameAction) {
        when (action) {
            is SignupNicknameAction.UpdateTextCount -> performUpdateTextCount(action.count)
            is SignupNicknameAction.SetNickname -> performSetNickname(action.nickname)
            is SignupNicknameAction.UpdateLength15 -> performUpdateLength15(action.length)
            is SignupNicknameAction.ActivateDuplicateButton -> performActivateDuplicateButton()
            is SignupNicknameAction.ResetState -> performResetState()
            is SignupNicknameAction.CheckDuplicate -> performCheckDuplicate(action.userId)
        }
    }

    private fun performUpdateTextCount(count: Int) {
        val countText = "($count/15)"
        updateState { copy(textCount = countText) }
    }

    private fun performSetNickname(newNickname: String) {
        updateState {
            copy(
                nickname = newNickname,
                isDuplicateCheckEnabled = newNickname.isNotEmpty()
            )
        }
    }

    private fun performUpdateLength15(length: Int) {
        val messageType = if (length >= 15) {
            DuplicateCheckMessageType.LENGTH_EXCEEDED
        } else {
            DuplicateCheckMessageType.CHECK_REQUIRED
        }
        updateState {
            copy(
                duplicateCheckMessageType = messageType,
                duplicateCheckMessage = messageType.message
            )
        }
    }

    private fun performActivateDuplicateButton() {
        updateState {
            copy(
                isDuplicateCheckEnabled = true,
                duplicateCheckButtonTextColor = 0xFF000000.toInt(),
                duplicateCheckButtonBackground = com.toyou.toyouandroid.R.drawable.signupnickname_doublecheck_activate
            )
        }
    }

    private fun performResetState() {
        updateState {
            SignupNicknameUiState()
        }
    }

    private fun performCheckDuplicate(userId: Int) {
        val nickname = currentState.nickname
        if (nickname.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = profileRepository.checkNickname(nickname, userId)
                if (response.isSuccessful) {
                    val exists = response.body()?.result?.exists ?: false
                    performHandleNicknameCheckResult(exists)
                } else {
                    performHandleNicknameCheckError()
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error checking nickname")
                updateState {
                    copy(
                        duplicateCheckMessageType = DuplicateCheckMessageType.SERVER_ERROR,
                        duplicateCheckMessage = DuplicateCheckMessageType.SERVER_ERROR.message,
                        isNicknameValid = false,
                        isNextButtonEnabled = false
                    )
                }
                sendEvent(SignupNicknameEvent.ServerError)
            }
        }
    }

    private fun performHandleNicknameCheckResult(exists: Boolean) {
        val messageType = if (!exists) {
            DuplicateCheckMessageType.AVAILABLE
        } else {
            DuplicateCheckMessageType.ALREADY_IN_USE
        }

        val messageColor = if (!exists) 0xFFEA9797.toInt() else 0xFFFF0000.toInt()

        updateState {
            copy(
                duplicateCheckMessageType = messageType,
                duplicateCheckMessage = messageType.message,
                duplicateCheckMessageColor = messageColor,
                isNicknameValid = !exists,
                isNextButtonEnabled = !exists,
                nextButtonTextColor = if (!exists) 0xFF000000.toInt() else nextButtonTextColor,
                nextButtonBackground = if (!exists) com.toyou.toyouandroid.R.drawable.next_button_enabled else nextButtonBackground
            )
        }
    }

    private fun performHandleNicknameCheckError() {
        updateState {
            copy(
                duplicateCheckMessageType = DuplicateCheckMessageType.CHECK_FAILED,
                duplicateCheckMessage = DuplicateCheckMessageType.CHECK_FAILED.message,
                isNicknameValid = false,
                isNextButtonEnabled = false
            )
        }
    }

    fun updateTextCount(count: Int) = onAction(SignupNicknameAction.UpdateTextCount(count))

    fun setNickname(newNickname: String) = onAction(SignupNicknameAction.SetNickname(newNickname))

    fun updateLength15(length: Int) = onAction(SignupNicknameAction.UpdateLength15(length))

    fun duplicateBtnActivate() = onAction(SignupNicknameAction.ActivateDuplicateButton)

    fun resetState() = onAction(SignupNicknameAction.ResetState)

    fun checkDuplicate(userId: Int) = onAction(SignupNicknameAction.CheckDuplicate(userId))
}
