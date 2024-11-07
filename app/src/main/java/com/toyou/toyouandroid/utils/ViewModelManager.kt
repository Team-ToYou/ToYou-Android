package com.toyou.toyouandroid.utils

import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel

class ViewModelManager(
    private val signupNicknameViewModel: SignupNicknameViewModel,
    private val homeViewModel: HomeViewModel,
    ) {
    fun resetAllViewModels() {
        signupNicknameViewModel.resetState()
        homeViewModel.resetState()
    }
}
