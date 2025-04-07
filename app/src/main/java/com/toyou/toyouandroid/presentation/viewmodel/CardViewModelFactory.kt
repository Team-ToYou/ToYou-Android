package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.utils.TokenManager

class CardViewModelFactory (
    private val repository: CreateRepository,
    private val tokenManager: TokenManager
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CardViewModel(tokenManager, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}