package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.utils.TokenStorage

class NoticeViewModelFactory(
    private val repository: NoticeRepository,
    private val authService: AuthService,
    private val tokenStorage: TokenStorage
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoticeViewModel(repository, authService, tokenStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}