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
class SignupAgreeViewModel @Inject constructor() : MviViewModel<SignupAgreeUiState, SignupAgreeEvent, SignupAgreeAction>(
    SignupAgreeUiState()
) {

    private val _imageStates = MutableLiveData(listOf(false, false, false, false))
    val imageStates: LiveData<List<Boolean>> get() = _imageStates

    private val _isNextButtonEnabled = MutableLiveData(false)
    val isNextButtonEnabled: LiveData<Boolean> get() = _isNextButtonEnabled

    private val _nextButtonTextColor = MutableLiveData<Int>().apply {
        value = 0xFFA6A6A6.toInt()
    }
    val nextButtonTextColor: LiveData<Int> get() = _nextButtonTextColor

    private val _nextButtonBackground = MutableLiveData<Int>().apply {
        value = R.drawable.next_button
    }
    val nextButtonBackground: LiveData<Int> get() = _nextButtonBackground

    private val targetImageResId = R.drawable.checkbox_checked

    init {
        viewModelScope.launch {
            state.collect { newState ->
                _imageStates.value = newState.imageStates
                _isNextButtonEnabled.value = newState.isNextButtonEnabled
                _nextButtonTextColor.value = newState.nextButtonTextColor
                _nextButtonBackground.value = newState.nextButtonBackground
            }
        }
    }

    override fun handleAction(action: SignupAgreeAction) {
        when (action) {
            is SignupAgreeAction.ImageClicked -> performImageClicked(action.index, action.newImageResId)
        }
    }

    private fun performImageClicked(index: Int, newImageResId: Int) {
        val newImageStates = currentState.imageStates.toMutableList()

        if (index == 0) {
            val newState = newImageResId == targetImageResId
            for (i in newImageStates.indices) {
                newImageStates[i] = newState
            }
        } else {
            newImageStates[index] = newImageResId == targetImageResId
        }

        val isSecondImageChecked = newImageStates[1]
        val isThirdImageChecked = newImageStates[2]
        val isFourthImageChecked = newImageStates[3]
        val allRequiredImagesChecked = isSecondImageChecked && isThirdImageChecked && isFourthImageChecked

        val nextButtonTextColor: Int
        val nextButtonBackground: Int

        if (allRequiredImagesChecked) {
            nextButtonTextColor = 0xFF000000.toInt()
            nextButtonBackground = R.drawable.next_button_enabled
        } else {
            nextButtonTextColor = 0xFFA6A6A6.toInt()
            nextButtonBackground = R.drawable.next_button
        }

        updateState {
            copy(
                imageStates = newImageStates,
                isNextButtonEnabled = allRequiredImagesChecked,
                nextButtonTextColor = nextButtonTextColor,
                nextButtonBackground = nextButtonBackground
            )
        }
    }

    fun onImageClicked(index: Int, newImageResId: Int) {
        onAction(SignupAgreeAction.ImageClicked(index, newImageResId))
    }
}
