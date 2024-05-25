package com.toyou.toyouandroid.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MypageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is mypage Fragment"
    }
    val text: LiveData<String> = _text
}