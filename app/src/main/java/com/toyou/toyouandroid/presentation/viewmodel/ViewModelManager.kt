package com.toyou.toyouandroid.presentation.viewmodel

import com.toyou.toyouandroid.presentation.fragment.home.HomeAction
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel

class ViewModelManager(
    private val signupNicknameViewModel: SignupNicknameViewModel,
    private val homeViewModel: HomeViewModel,
) {
    fun resetAllViewModels() {
        signupNicknameViewModel.resetState()
        homeViewModel.onAction(HomeAction.ResetState)
    }
}
