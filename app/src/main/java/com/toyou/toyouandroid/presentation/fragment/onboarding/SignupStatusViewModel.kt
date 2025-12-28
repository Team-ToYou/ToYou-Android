package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import com.toyou.toyouandroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupStatusViewModel @Inject constructor() : MviViewModel<SignupStatusUiState, SignupStatusEvent, SignupStatusAction>(SignupStatusUiState()) {

    private val _uiStateLiveData = MutableLiveData(SignupStatusUiState())
    val uiState: LiveData<SignupStatusUiState> get() = _uiStateLiveData

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

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    init {
        viewModelScope.launch {
            state.collect { newState ->
                _uiStateLiveData.value = newState
                _selectedButtonId.value = newState.selectedButtonId
                _isNextButtonEnabled.value = newState.isNextButtonEnabled
                _nextButtonTextColor.value = newState.nextButtonTextColor
                _nextButtonBackground.value = newState.nextButtonBackground
                _status.value = newState.status
            }
        }
    }

    override fun handleAction(action: SignupStatusAction) {
        when (action) {
            is SignupStatusAction.ButtonClicked -> performButtonClicked(action.buttonId)
        }
    }

    private fun performButtonClicked(buttonId: Int) {
        if (currentState.selectedButtonId == buttonId) return

        val status = when (buttonId) {
            R.id.signup_status_option_1 -> "SCHOOL"
            R.id.signup_status_option_2 -> "COLLEGE"
            R.id.signup_status_option_3 -> "OFFICE"
            R.id.signup_status_option_4 -> "ETC"
            else -> ""
        }

        updateState {
            copy(
                selectedButtonId = buttonId,
                status = status,
                isNextButtonEnabled = true,
                nextButtonTextColor = 0xFF000000.toInt(),
                nextButtonBackground = R.drawable.next_button_enabled
            )
        }
    }

    fun getButtonBackground(buttonId: Int): Int {
        return if (currentState.selectedButtonId == buttonId) {
            R.drawable.signupnickname_doublecheck_activate
        } else {
            R.drawable.signupnickname_input
        }
    }

    fun onButtonClicked(buttonId: Int) {
        onAction(SignupStatusAction.ButtonClicked(buttonId))
    }
}
