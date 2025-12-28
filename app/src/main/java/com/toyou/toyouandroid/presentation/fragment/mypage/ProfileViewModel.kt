package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.domain.profile.repository.IProfileRepository
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: IProfileRepository,
    private val tokenManager: TokenManager
) : MviViewModel<ProfileUiState, ProfileEvent, ProfileAction>(ProfileUiState()) {

    private val _textCount = MutableLiveData("0/15")
    val textCount: LiveData<String> get() = _textCount

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _duplicateCheckButtonTextColor = MutableLiveData(0xFFA6A6A6.toInt())
    val duplicateCheckButtonTextColor: LiveData<Int> get() = _duplicateCheckButtonTextColor

    private val _duplicateCheckButtonBackground = MutableLiveData(com.toyou.toyouandroid.R.drawable.next_button)
    val duplicateCheckButtonBackground: LiveData<Int> get() = _duplicateCheckButtonBackground

    private val _duplicateCheckMessage = MutableLiveData("중복된 닉네임인지 확인해주세요")
    val duplicateCheckMessage: LiveData<String> get() = _duplicateCheckMessage

    private val _duplicateCheckMessageColor = MutableLiveData(0xFF000000.toInt())
    val duplicateCheckMessageColor: LiveData<Int> get() = _duplicateCheckMessageColor

    private val _nextButtonBackground = MutableLiveData(com.toyou.toyouandroid.R.drawable.next_button)
    val nextButtonBackground: LiveData<Int> get() = _nextButtonBackground

    private val _nextButtonTextColor = MutableLiveData(0xFFA6A6A6.toInt())
    val nextButtonTextColor: LiveData<Int> get() = _nextButtonTextColor

    private val _isNextButtonEnabled = MutableLiveData(false)
    val isNextButtonEnabled: LiveData<Boolean> get() = _isNextButtonEnabled

    private val _nicknameChangedSuccess = MutableLiveData<Boolean>()
    val nicknameChangedSuccess: LiveData<Boolean> get() = _nicknameChangedSuccess

    private val _duplicateCheckMessageType = MutableLiveData<DuplicateCheckMessageType>()
    val duplicateCheckMessageType: LiveData<DuplicateCheckMessageType> get() = _duplicateCheckMessageType

    @Deprecated("Use state instead", ReplaceWith("state"))
    private val _uiState = MutableLiveData(ProfileUiState())
    @Deprecated("Use state instead", ReplaceWith("state"))
    val uiState: LiveData<ProfileUiState> get() = _uiState

    init {
        updateState { copy(title = "회원가입") }
        performSyncLegacyLiveData()
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

    private fun performSyncLegacyLiveData() {
        state.onEach { uiState ->
            _uiState.value = uiState
            _textCount.value = uiState.textCount
            _nickname.value = uiState.nickname
            _duplicateCheckMessage.value = uiState.duplicateCheckMessage
            _isNextButtonEnabled.value = uiState.isNextButtonEnabled
        }.launchIn(viewModelScope)
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
        _duplicateCheckMessageType.value = messageType
        updateState { copy(duplicateCheckMessage = messageType.message) }
    }

    private fun performDuplicateBtnActivate() {
        updateState { copy(isDuplicateCheckEnabled = true) }
        _duplicateCheckButtonTextColor.value = 0xFF000000.toInt()
        _duplicateCheckButtonBackground.value = com.toyou.toyouandroid.R.drawable.signupnickname_doublecheck_activate
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
        _duplicateCheckMessageType.value = DuplicateCheckMessageType.CHECK_REQUIRED
    }

    private fun performCheckDuplicate(userNickname: String, userId: Int) {
        val nickname = currentState.nickname
        if (nickname.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = profileRepository.checkNickname(nickname, userId)
                if (response.isSuccessful) {
                    val exists = response.body()?.result?.exists ?: false
                    performHandleNicknameCheckResult(exists, userNickname, nickname)
                } else {
                    performHandleNicknameCheckError()
                    tokenManager.refreshToken(
                        onSuccess = { performCheckDuplicate(userNickname, userId) },
                        onFailure = { Timber.e("Failed to refresh token and check nickname") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error checking nickname")
                sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.SERVER_ERROR))
                _duplicateCheckMessageType.value = DuplicateCheckMessageType.SERVER_ERROR
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
        _duplicateCheckMessageType.value = messageType
        val isValid = !exists || (userNickname == nickname)

        updateState {
            copy(
                duplicateCheckMessage = messageType.message,
                isNicknameValid = isValid,
                isNextButtonEnabled = isValid
            )
        }

        _duplicateCheckMessageColor.value = when {
            !exists -> 0xFFEA9797.toInt()
            else -> 0xFFFF0000.toInt()
        }
        if (isValid) {
            _nextButtonTextColor.value = 0xFF000000.toInt()
            _nextButtonBackground.value = com.toyou.toyouandroid.R.drawable.next_button_enabled
        }
    }

    private fun performHandleNicknameCheckError() {
        sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.CHECK_FAILED))
        _duplicateCheckMessageType.value = DuplicateCheckMessageType.CHECK_FAILED
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
                val response = profileRepository.updateNickname(nickname)
                if (response.isSuccessful) {
                    _nicknameChangedSuccess.postValue(true)
                    sendEvent(ProfileEvent.NicknameChangedSuccess)
                    Timber.tag("changeNickname").d("$response")
                } else {
                    sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.UPDATE_FAILED))
                    _duplicateCheckMessageType.value = DuplicateCheckMessageType.UPDATE_FAILED
                    updateState { copy(duplicateCheckMessage = DuplicateCheckMessageType.UPDATE_FAILED.message) }
                    tokenManager.refreshToken(
                        onSuccess = { performChangeNickname() },
                        onFailure = { Timber.e("Failed to refresh token and update nickname") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error updating nickname")
                sendEvent(ProfileEvent.DuplicateCheckMessageChanged(DuplicateCheckMessageType.SERVER_ERROR))
                _duplicateCheckMessageType.value = DuplicateCheckMessageType.SERVER_ERROR
                updateState { copy(duplicateCheckMessage = DuplicateCheckMessageType.SERVER_ERROR.message) }
            }
        }
    }

    private fun performChangeStatus() {
        val status = currentState.status
        if (status.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = profileRepository.updateStatus(status)
                if (response.isSuccessful) {
                    Timber.tag("changeStatus").d("${response.body()}")
                } else {
                    tokenManager.refreshToken(
                        onSuccess = { performChangeStatus() },
                        onFailure = { Timber.e("Failed to refresh token and update status") }
                    )
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
