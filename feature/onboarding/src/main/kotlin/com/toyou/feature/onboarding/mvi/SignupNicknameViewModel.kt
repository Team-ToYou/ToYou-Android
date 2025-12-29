package com.toyou.feature.onboarding.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.repository.IProfileRepository
import com.toyou.core.domain.model.DuplicateCheckMessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignupNicknameViewModel @Inject constructor(
    private val profileRepository: IProfileRepository
) : MviViewModel<SignupNicknameUiState, SignupNicknameEvent, SignupNicknameAction>(
    initialState = SignupNicknameUiState()
) {

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
                duplicateCheckButtonBackground = com.toyou.feature.onboarding.R.drawable.signupnickname_doublecheck_activate
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
                when (val result = profileRepository.checkNickname(nickname, userId)) {
                    is DomainResult.Success -> {
                        val exists = result.data.exists
                        performHandleNicknameCheckResult(exists)
                    }
                    is DomainResult.Error -> {
                        performHandleNicknameCheckError()
                    }
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
                nextButtonBackground = if (!exists) com.toyou.feature.onboarding.R.drawable.next_button_enabled else nextButtonBackground
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
