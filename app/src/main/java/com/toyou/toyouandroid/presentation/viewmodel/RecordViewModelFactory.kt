package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.presentation.fragment.record.CardInfoViewModel
import com.toyou.toyouandroid.presentation.fragment.record.friend.FriendCardViewModel
import com.toyou.toyouandroid.presentation.fragment.record.friend.FriendRecordViewModel
import com.toyou.toyouandroid.presentation.fragment.record.my.MyCardViewModel
import com.toyou.toyouandroid.presentation.fragment.record.my.MyRecordViewModel
import com.toyou.toyouandroid.utils.TokenManager

class RecordViewModelFactory(
    private val repository: RecordRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyRecordViewModel(repository, tokenManager) as T
        } else if (modelClass.isAssignableFrom(FriendRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendRecordViewModel(repository, tokenManager) as T
        } else if (modelClass.isAssignableFrom(MyCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyCardViewModel(repository, tokenManager) as T
        } else if (modelClass.isAssignableFrom(FriendCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendCardViewModel(repository, tokenManager) as T
        } else if (modelClass.isAssignableFrom(CardInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardInfoViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}