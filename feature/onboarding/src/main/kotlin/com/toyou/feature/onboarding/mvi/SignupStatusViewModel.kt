package com.toyou.feature.onboarding.viewmodel

import com.toyou.core.common.mvi.MviViewModel
import com.toyou.core.domain.model.StatusType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupStatusViewModel @Inject constructor() : MviViewModel<SignupStatusUiState, SignupStatusEvent, SignupStatusAction>(SignupStatusUiState()) {

    override fun handleAction(action: SignupStatusAction) {
        when (action) {
            is SignupStatusAction.StatusSelected -> performStatusSelected(action.statusType)
            is SignupStatusAction.Signup -> { /* Handled by LoginViewModel */ }
        }
    }

    private fun performStatusSelected(statusType: StatusType) {
        updateState {
            copy(
                selectedStatusType = statusType,
                status = statusType.value,
                isNextButtonEnabled = true
            )
        }
    }

    fun onStatusSelected(statusType: StatusType) {
        onAction(SignupStatusAction.StatusSelected(statusType))
    }
}
