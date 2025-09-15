package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toyou.toyouandroid.domain.profile.repository.ProfileRepository
import com.toyou.toyouandroid.presentation.fragment.mypage.DuplicateCheckMessageType
import com.toyou.toyouandroid.presentation.fragment.mypage.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignupNicknameViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableLiveData(ProfileUiState())
    val uiState: LiveData<ProfileUiState> get() = _uiState
    
    // 데이터 바인딩 호환성을 위한 프로퍼티
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
    
    fun resetState() {
        _uiState.value = ProfileUiState(title = "회원가입")
        _duplicateCheckMessageType.value = DuplicateCheckMessageType.CHECK_REQUIRED
    }
    
    fun checkDuplicate(userId: Int) {
        val nickname = _uiState.value?.nickname ?: return
        
        viewModelScope.launch {
            try {
                val response = profileRepository.checkNickname(nickname, userId)
                if (response.isSuccessful) {
                    val exists = response.body()?.result?.exists ?: false
                    handleNicknameCheckResult(exists)
                } else {
                    handleNicknameCheckError()
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
    
    private fun handleNicknameCheckResult(exists: Boolean) {
        val messageType = if (!exists) {
            DuplicateCheckMessageType.AVAILABLE
        } else {
            DuplicateCheckMessageType.ALREADY_IN_USE
        }
        
        _duplicateCheckMessageType.value = messageType
        _uiState.value = _uiState.value?.copy(
            duplicateCheckMessage = messageType.message,
            isNicknameValid = !exists,
            isNextButtonEnabled = !exists
        )
        
        // 호환성 프로퍼티 업데이트
        (duplicateCheckMessage as MutableLiveData).value = messageType.message
        (duplicateCheckMessageColor as MutableLiveData).value = if (!exists) 0xFFEA9797.toInt() else 0xFFFF0000.toInt()
        (isNextButtonEnabled as MutableLiveData).value = !exists
        if (!exists) {
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
}