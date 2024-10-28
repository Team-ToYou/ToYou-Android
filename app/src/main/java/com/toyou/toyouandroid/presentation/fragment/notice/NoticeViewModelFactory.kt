package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.notice.NoticeRepository

class NoticeViewModelFactory(
    private val repository: NoticeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoticeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}