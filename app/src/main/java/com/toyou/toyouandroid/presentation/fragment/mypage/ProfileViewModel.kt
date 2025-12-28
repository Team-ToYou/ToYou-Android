package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.profile.repository.ProfileRepository
import com.toyou.toyouandroid.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _uiState = MutableLiveData(ProfileUiState())
    val uiState: LiveData<ProfileUiState> get() = _uiState
    
    // 데이터 바인딩 호환성을 위한 프로퍼티
    val textCount: LiveData<String> = MutableLiveData("0/15")
    val nickname: LiveData<String> = MutableLiveData()
    val duplicateCheckButtonTextColor: LiveData<Int> = MutableLiveData(0xFFA6A6A6.toInt())
    val duplicateCheckButtonBackground: LiveData<Int> = MutableLiveData(com.toyou.toyouandroid.R.drawable.next_button)
    val duplicateCheckMessage: LiveData<String> = MutableLiveData("중복된 닉네임인지 확인해주세요")
    val duplicateCheckMessageColor: LiveData<Int> = MutableLiveData(0xFF000000.toInt())
    val nextButtonBackground: LiveData<Int> = MutableLiveData(com.toyou.toyouandroid.R.drawable.next_button)
    val nextButtonTextColor: LiveData<Int> = MutableLiveData(0xFFA6A6A6.toInt())
    val isNextButtonEnabled: LiveData<Boolean> = MutableLiveData(false)
    
    private val _nicknameChangedSuccess = MutableLiveData<Boolean>()
    val nicknameChangedSuccess: LiveData<Boolean> get() = _nicknameChangedSuccess
    
    private val _duplicateCheckMessageType = MutableLiveData<DuplicateCheckMessageType>()
    val duplicateCheckMessageType: LiveData<DuplicateCheckMessageType> get() = _duplicateCheckMessageType
    
    init {
        _uiState.value = ProfileUiState(title = "회원가입")
    }
    
    fun updateTextCount(count: Int) {
        val countText = "($count/15)"
        _uiState.value = _uiState.value?.copy(
            textCount = countText
        )
        (textCount as MutableLiveData).value = countText
    }
    
    fun setNickname(newNickname: String) {
        _uiState.value = _uiState.value?.copy(
            nickname = newNickname,
            isDuplicateCheckEnabled = newNickname.isNotEmpty()
        )
        (nickname as MutableLiveData).value = newNickname
    }
    
    fun updateLength15(length: Int) {
        val messageType = if (length >= 15) {
            DuplicateCheckMessageType.LENGTH_EXCEEDED
        } else {
            DuplicateCheckMessageType.CHECK_REQUIRED
        }
        _duplicateCheckMessageType.value = messageType
        _uiState.value = _uiState.value?.copy(
            duplicateCheckMessage = messageType.message
        )
    }
    
    fun duplicateBtnActivate() {
        _uiState.value = _uiState.value?.copy(
            isDuplicateCheckEnabled = true
        )
        (duplicateCheckButtonTextColor as MutableLiveData).value = 0xFF000000.toInt()
        (duplicateCheckButtonBackground as MutableLiveData).value = com.toyou.toyouandroid.R.drawable.signupnickname_doublecheck_activate
    }
    
    fun resetNicknameEditState() {
        _uiState.value = _uiState.value?.copy(
            duplicateCheckMessage = DuplicateCheckMessageType.CHECK_REQUIRED.message,
            isNextButtonEnabled = false,
            isNicknameValid = false
        )
        _duplicateCheckMessageType.value = DuplicateCheckMessageType.CHECK_REQUIRED
    }
    
    fun checkDuplicate(userNickname: String, userId: Int) {
        val nickname = _uiState.value?.nickname ?: return
        
        viewModelScope.launch {
            try {
                val response = profileRepository.checkNickname(nickname, userId)
                if (response.isSuccessful) {
                    val exists = response.body()?.result?.exists ?: false
                    handleNicknameCheckResult(exists, userNickname, nickname)
                } else {
                    handleNicknameCheckError()
                    tokenManager.refreshToken(
                        onSuccess = { checkDuplicate(userNickname, userId) },
                        onFailure = { Timber.e("Failed to refresh token and check nickname") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error checking nickname")
                _duplicateCheckMessageType.value = DuplicateCheckMessageType.SERVER_ERROR
                _uiState.value = _uiState.value?.copy(
                    duplicateCheckMessage = DuplicateCheckMessageType.SERVER_ERROR.message,
                    isNicknameValid = false,
                    isNextButtonEnabled = false
                )
            }
        }
    }
    
    private fun handleNicknameCheckResult(exists: Boolean, userNickname: String, nickname: String) {
        val messageType = when {
            !exists -> DuplicateCheckMessageType.AVAILABLE
            userNickname == nickname -> DuplicateCheckMessageType.ALREADY_IN_USE_SAME
            else -> DuplicateCheckMessageType.ALREADY_IN_USE
        }
        
        _duplicateCheckMessageType.value = messageType
        val isValid = !exists || (userNickname == nickname)
        
        _uiState.value = _uiState.value?.copy(
            duplicateCheckMessage = messageType.message,
            isNicknameValid = isValid,
            isNextButtonEnabled = isValid
        )
        
        // 호환성 프로퍼티 업데이트
        (duplicateCheckMessage as MutableLiveData).value = messageType.message
        (duplicateCheckMessageColor as MutableLiveData).value = when {
            !exists -> 0xFFEA9797.toInt()
            else -> 0xFFFF0000.toInt()
        }
        (isNextButtonEnabled as MutableLiveData).value = isValid
        if (isValid) {
            (nextButtonTextColor as MutableLiveData).value = 0xFF000000.toInt()
            (nextButtonBackground as MutableLiveData).value = com.toyou.toyouandroid.R.drawable.next_button_enabled
        }
    }
    
    private fun handleNicknameCheckError() {
        _duplicateCheckMessageType.value = DuplicateCheckMessageType.CHECK_FAILED
        _uiState.value = _uiState.value?.copy(
            duplicateCheckMessage = DuplicateCheckMessageType.CHECK_FAILED.message,
            isNicknameValid = false,
            isNextButtonEnabled = false
        )
    }
    
    fun changeNickname() {
        val nickname = _uiState.value?.nickname ?: return
        
        viewModelScope.launch {
            try {
                val response = profileRepository.updateNickname(nickname)
                if (response.isSuccessful) {
                    _nicknameChangedSuccess.postValue(true)
                    Timber.tag("changeNickname").d("$response")
                } else {
                    _duplicateCheckMessageType.value = DuplicateCheckMessageType.UPDATE_FAILED
                    _uiState.value = _uiState.value?.copy(
                        duplicateCheckMessage = DuplicateCheckMessageType.UPDATE_FAILED.message
                    )
                    tokenManager.refreshToken(
                        onSuccess = { changeNickname() },
                        onFailure = { Timber.e("Failed to refresh token and update nickname") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error updating nickname")
                _duplicateCheckMessageType.value = DuplicateCheckMessageType.SERVER_ERROR
                _uiState.value = _uiState.value?.copy(
                    duplicateCheckMessage = DuplicateCheckMessageType.SERVER_ERROR.message
                )
            }
        }
    }
    
    fun changeStatus() {
        val status = _uiState.value?.status ?: return
        
        viewModelScope.launch {
            try {
                val response = profileRepository.updateStatus(status)
                if (response.isSuccessful) {
                    Timber.tag("changeStatus").d("${response.body()}")
                } else {
                    tokenManager.refreshToken(
                        onSuccess = { changeStatus() },
                        onFailure = { Timber.e("Failed to refresh token and update status") }
                    )
                }
            } catch (e: Exception) {
                Timber.tag("API Failure").e(e, "Error updating status")
            }
        }
    }
    
    fun onStatusButtonClicked(statusType: StatusType) {
        if (_uiState.value?.selectedStatusType == statusType) return
        
        _uiState.value = _uiState.value?.copy(
            selectedStatusType = statusType,
            status = statusType.value,
            isNextButtonEnabled = true
        )
    }
}

enum class DuplicateCheckMessageType(val message: String) {
    CHECK_REQUIRED("중복된 닉네임인지 확인해주세요"),
    LENGTH_EXCEEDED("15자 이내로 입력해주세요."),
    AVAILABLE("사용 가능한 닉네임입니다."),
    ALREADY_IN_USE("이미 사용 중인 닉네임입니다."),
    ALREADY_IN_USE_SAME("이미 사용 중인 닉네임입니다."),
    CHECK_FAILED("닉네임 확인에 실패했습니다."),
    UPDATE_FAILED("닉네임 변경에 실패했습니다."),
    SERVER_ERROR("서버에 연결할 수 없습니다.")
}