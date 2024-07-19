package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R

class SignupNicknameViewModel : ViewModel() {

    private val _textCount = MutableLiveData("0/15")
    val textCount: LiveData<String> get() = _textCount

    fun updateTextCount(count: Int) {
        _textCount.value = "$count/15"
    }

    private val inputText = MutableLiveData<String>()

    private val _isDuplicateCheckEnabled = MutableLiveData(false)
    val isDuplicateCheckEnabled: LiveData<Boolean> = _isDuplicateCheckEnabled

    private val _duplicateCheckMessage = MutableLiveData<String>().apply {
        value = "중복된 닉네임인지 확인해주세요"
    }
    val duplicateCheckMessage: LiveData<String> = _duplicateCheckMessage

    private val _duplicateCheckMessageColor = MutableLiveData<Int>().apply {
        value = 0xFF000000.toInt()
    }
    val duplicateCheckMessageColor: LiveData<Int> = _duplicateCheckMessageColor

    private val _duplicateCheckButtonTextColor = MutableLiveData<Int>().apply {
        value = 0xFFA6A6A6.toInt()
    }
    val duplicateCheckButtonTextColor: LiveData<Int> = _duplicateCheckButtonTextColor

    private val _duplicateCheckButtonBackground = MutableLiveData<Int>().apply {
        value = R.drawable.next_button
    }
    val duplicateCheckButtonBackground: LiveData<Int> = _duplicateCheckButtonBackground


    private val _isNextButtonEnabled = MutableLiveData(false)
    val isNextButtonEnabled: LiveData<Boolean> = _isNextButtonEnabled

    private val _nextButtonTextColor = MutableLiveData<Int>().apply {
        value = 0xFFA6A6A6.toInt()
    }
    val nextButtonTextColor: LiveData<Int> = _nextButtonTextColor

    private val _nextButtonBackground = MutableLiveData<Int>().apply {
        value = R.drawable.next_button
    }
    val nextButtonBackground: LiveData<Int> = _nextButtonBackground

    init {
        inputText.observeForever { text ->
            _isDuplicateCheckEnabled.value = !text.isNullOrEmpty()
        }
    }

    fun checkDuplicate() {
        _duplicateCheckMessage.value = "사용 가능한 닉네임입니다."
        _duplicateCheckMessageColor.value = 0xFFEA9797.toInt()

        _isNextButtonEnabled.value = true
        _nextButtonTextColor.value = 0xFF000000.toInt()
        _nextButtonBackground.value = R.drawable.next_button_enabled
    }

    fun duplicateBtnActivate() {
        _duplicateCheckButtonTextColor.value = 0xFF000000.toInt()
        _duplicateCheckButtonBackground.value = R.drawable.signupnickname_doublecheck_activate
    }

    fun onNextClicked() {
        // 다음 버튼 클릭 처리
    }
}