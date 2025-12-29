package com.toyou.feature.mypage.viewmodel

import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.data.utils.ApiErrorHandler
import com.toyou.core.domain.model.DomainResult
import com.toyou.core.domain.model.DuplicateCheckMessageType
import com.toyou.core.domain.model.StatusType
import com.toyou.core.domain.repository.IProfileRepository
import com.toyou.feature.mypage.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: IProfileRepository,
    private val errorHandler: ApiErrorHandler
) : MviViewModel<ProfileUiState, ProfileEvent, ProfileAction>(ProfileUiState()) {

    init {
        updateState { copy(title = "회원가입") }
    }

    override fun handleAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.UpdateTextCount -> performUpdateTextCount(action.count)
            is ProfileAction.SetNickname -> performSetNickname(action.nickname)
            is ProfileAction.UpdateLength15 -> performUpdateLength15(action.length)
            is ProfileAction.DuplicateBtnActivate -> performDuplicateBtnActivate()
            is ProfileAction.ResetNicknameEditState -> performResetNicknameEditState()
            is ProfileAction.CheckDuplicate -> performCheckDuplicate(action.userNickname, action.userId)
            is ProfileAction.ChangeNickname -> performChangeNickname()
            is ProfileAction.ChangeStatus -> performChangeStatus()
            is ProfileAction.OnStatusButtonClicked -> performOnStatusButtonClicked(action.statusType)
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
        sendEvent(ProfileEvent.DuplicateCheckMessageChanged(messageType))
        updateState { copy(duplicateCheckMessage = messageType.message) }
    }

    private fun performDuplicateBtnActivate() {
        updateState { copy(isDuplicateCheckEnabled = true) }
    }

    private fun performResetNicknameEditState() {
        updateState {
            copy(
                duplicateCheckMessage = DuplicateCheckMessageType.CHECK_REQUIRED.message,
                isNextButtonEnabled = false,
                isNicknameValid = false
            )
        }
        sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.CHECK_REQUIRED))
    }

    private fun performCheckDuplicate(userNickname: String, userId: Int) {
        val nickname = currentState.nickname
        if (nickname.isEmpty()) return

        viewModelScope.launch {
            try {
                when (val result = profileRepository.checkNickname(nickname, userId)) {
                    is DomainResult.Success -> {
                        val exists = result.data.exists
                        performHandleNicknameCheckResult(exists, userNickname, nickname)
                    }
                    is DomainResult.Error -> {
                        performHandleNicknameCheckError()
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performCheckDuplicate(userNickname, userId) },
                            onFailure = { Timber.e("Failed to refresh token and check nickname") },
                            tag = "ProfileViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error checking nickname")
                sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.SERVER_ERROR))
                updateState {
                    copy(
                        duplicateCheckMessage = DuplicateCheckMessageType.SERVER_ERROR.message,
                        isNicknameValid = false,
                        isNextButtonEnabled = false
                    )
                }
            }
        }
    }

    private fun performHandleNicknameCheckResult(exists: Boolean, userNickname: String, nickname: String) {
        val messageType = when {
            !exists -> DuplicateCheckMessageType.AVAILABLE
            userNickname == nickname -> DuplicateCheckMessageType.ALREADY_IN_USE_SAME
            else -> DuplicateCheckMessageType.ALREADY_IN_USE
        }

        sendEvent(ProfileEvent.DuplicateCheckMessageChanged(messageType))
        val isValid = !exists || (userNickname == nickname)

        updateState {
            copy(
                duplicateCheckMessage = messageType.message,
                isNicknameValid = isValid,
                isNextButtonEnabled = isValid
            )
        }
    }

    private fun performHandleNicknameCheckError() {
        sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.CHECK_FAILED))
        updateState {
            copy(
                duplicateCheckMessage = DuplicateCheckMessageType.CHECK_FAILED.message,
                isNicknameValid = false,
                isNextButtonEnabled = false
            )
        }
    }

    private fun performChangeNickname() {
        val nickname = currentState.nickname
        if (nickname.isEmpty()) return

        viewModelScope.launch {
            try {
                when (val result = profileRepository.updateNickname(nickname)) {
                    is DomainResult.Success -> {
                        sendEvent(ProfileEvent.NicknameChangedSuccess)
                        Timber.tag("changeNickname").d("Nickname updated successfully")
                    }
                    is DomainResult.Error -> {
                        sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.UPDATE_FAILED))
                        updateState { copy(duplicateCheckMessage = DuplicateCheckMessageType.UPDATE_FAILED.message) }
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performChangeNickname() },
                            onFailure = { Timber.e("Failed to refresh token and update nickname") },
                            tag = "ProfileViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error updating nickname")
                sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.SERVER_ERROR))
                updateState { copy(duplicateCheckMessage = DuplicateCheckMessageType.SERVER_ERROR.message) }
            }
        }
    }

    private fun performChangeStatus() {
        val status = currentState.status
        if (status.isEmpty()) return

        viewModelScope.launch {
            try {
                when (val result = profileRepository.updateStatus(status)) {
                    is DomainResult.Success -> {
                        Timber.tag("changeStatus").d("Status updated successfully")
                    }
                    is DomainResult.Error -> {
                        errorHandler.handleError(
                            error = result,
                            onRetry = { performChangeStatus() },
                            onFailure = { Timber.e("Failed to refresh token and update status") },
                            tag = "ProfileViewModel"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error updating status")
            }
        }
    }

    private fun performOnStatusButtonClicked(statusType: StatusType) {
        if (currentState.selectedStatusType == statusType) return

        updateState {
            copy(
                selectedStatusType = statusType,
                status = statusType.value,
                isNextButtonEnabled = true
            )
        }
    }

    @Deprecated("Use onAction(ProfileAction.UpdateTextCount) instead")
    fun updateTextCount(count: Int) {
        onAction(ProfileAction.UpdateTextCount(count))
    }

    @Deprecated("Use onAction(ProfileAction.SetNickname) instead")
    fun setNickname(newNickname: String) {
        onAction(ProfileAction.SetNickname(newNickname))
    }

    @Deprecated("Use onAction(ProfileAction.UpdateLength15) instead")
    fun updateLength15(length: Int) {
        onAction(ProfileAction.UpdateLength15(length))
    }

    @Deprecated("Use onAction(ProfileAction.DuplicateBtnActivate) instead")
    fun duplicateBtnActivate() {
        onAction(ProfileAction.DuplicateBtnActivate)
    }

    @Deprecated("Use onAction(ProfileAction.ResetNicknameEditState) instead")
    fun resetNicknameEditState() {
        onAction(ProfileAction.ResetNicknameEditState)
    }

    @Deprecated("Use onAction(ProfileAction.CheckDuplicate) instead")
    fun checkDuplicate(userNickname: String, userId: Int) {
        onAction(ProfileAction.CheckDuplicate(userNickname, userId))
    }

    @Deprecated("Use onAction(ProfileAction.ChangeNickname) instead")
    fun changeNickname() {
        onAction(ProfileAction.ChangeNickname)
    }

    @Deprecated("Use onAction(ProfileAction.ChangeStatus) instead")
    fun changeStatus() {
        onAction(ProfileAction.ChangeStatus)
    }

    @Deprecated("Use onAction(ProfileAction.OnStatusButtonClicked) instead")
    fun onStatusButtonClicked(statusType: StatusType) {
        onAction(ProfileAction.OnStatusButtonClicked(statusType))
    }
}
