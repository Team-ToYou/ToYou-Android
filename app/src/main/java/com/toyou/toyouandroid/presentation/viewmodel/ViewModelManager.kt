package com.toyou.toyouandroid.presentation.viewmodel

import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel

class ViewModelManager(
    private val signupNicknameViewModel: SignupNicknameViewModel,
    private val homeViewModel: HomeViewModel,
    ) {
    fun resetAllViewModels() {
        signupNicknameViewModel.resetState()
        homeViewModel.resetState()
    }
}
