package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.R

class SignupStatusViewModel : ViewModel() {

    private val _selectedButtonId = MutableLiveData<Int?>(null)
    val selectedButtonId: LiveData<Int?> get() = _selectedButtonId

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

    fun onButtonClicked(buttonId: Int) {
        if (_selectedButtonId.value == buttonId) return
        _selectedButtonId.value = buttonId

        _isNextButtonEnabled.value = true
        _nextButtonTextColor.value = 0xFF000000.toInt()
        _nextButtonBackground.value = R.drawable.next_button_enabled
    }

    fun getButtonBackground(buttonId: Int): Int {
        return if (_selectedButtonId.value == buttonId) {
            R.drawable.signupnickname_doublecheck_activate // 선택된 상태의 배경
        } else {
            R.drawable.signupnickname_input // 기본 상태의 배경
        }
    }
}