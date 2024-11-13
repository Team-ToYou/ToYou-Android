package com.toyou.toyouandroid.presentation.fragment.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.presentation.fragment.record.friend.FriendCardViewModel
import com.toyou.toyouandroid.presentation.fragment.record.my.MyCardViewModel
import com.toyou.toyouandroid.utils.TokenStorage

class CardInfoViewModelFactory(private val tokenStorage: TokenStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardInfoViewModel(tokenStorage) as T
        } else if (modelClass.isAssignableFrom(MyCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyCardViewModel(tokenStorage) as T
        } else if (modelClass.isAssignableFrom(FriendCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendCardViewModel(tokenStorage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
