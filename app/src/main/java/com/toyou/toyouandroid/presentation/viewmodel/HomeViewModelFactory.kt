package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.presentation.fragment.emotionstamp.HomeOptionViewModel
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.fragment.mypage.ProfileViewModel
import com.toyou.toyouandroid.utils.TokenManager

class HomeViewModelFactory(
    private val tokenManager: TokenManager,
    private val repository: HomeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeOptionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeOptionViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(tokenManager, repository) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(tokenManager) as T
        }
        /*else if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(tokenManager) as T
        }
        else if (modelClass.isAssignableFrom(UserViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return UserViewModel(tokenManager) as T
        }*/
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}