package com.toyou.toyouandroid.presentation.fragment.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.presentation.fragment.emotionstamp.HomeOptionViewModel
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.fragment.mypage.MypageViewModel
import com.toyou.toyouandroid.presentation.fragment.mypage.ProfileViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage

class AuthViewModelFactory(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authService, tokenStorage) as T
        } else if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(tokenStorage) as T
        } else if (modelClass.isAssignableFrom(HomeOptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeOptionViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(MypageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MypageViewModel(authService, tokenStorage, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}