package com.toyou.toyouandroid.presentation.fragment.record.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.toyou.toyouandroid.presentation.fragment.record.friend.FriendRecordViewModel
import com.toyou.toyouandroid.presentation.fragment.record.my.MyRecordViewModel

class RecordViewModelFactory(
    private val repository: RecordRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyRecordViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FriendRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}