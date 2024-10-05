package com.toyou.toyouandroid.presentation.fragment.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.utils.TokenStorage

class CardInfoViewModelFactory(private val tokenStorage: TokenStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardInfoViewModel(tokenStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
