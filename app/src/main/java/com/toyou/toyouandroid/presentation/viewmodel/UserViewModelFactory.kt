package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.utils.TokenManager

class UserViewModelFactory (
    private val repository: CreateRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(tokenManager, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}