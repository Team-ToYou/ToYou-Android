package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.presentation.fragment.notice.NoticeViewModel
import com.toyou.toyouandroid.utils.TokenManager

class NoticeViewModelFactory(
    private val repository: NoticeRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoticeViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}