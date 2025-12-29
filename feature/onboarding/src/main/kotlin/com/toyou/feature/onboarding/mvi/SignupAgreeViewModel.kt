package com.toyou.feature.onboarding.viewmodel

import com.toyou.core.common.mvi.MviViewModel
import com.toyou.feature.onboarding.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupAgreeViewModel @Inject constructor() : MviViewModel<SignupAgreeUiState, SignupAgreeEvent, SignupAgreeAction>(
    SignupAgreeUiState()
) {

    private val targetImageResId = R.drawable.checkbox_checked

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
