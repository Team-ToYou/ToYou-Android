package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.utils.TokenManager

class SocialViewModelFactory(
    private val repository: SocialRepository,
    private val tokenManager: TokenManager,
    private val fcmRepository: FCMRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SocialViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SocialViewModel(repository, tokenManager,fcmRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}