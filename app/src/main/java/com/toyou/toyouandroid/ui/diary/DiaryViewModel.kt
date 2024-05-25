package com.toyou.toyouandroid.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiaryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is diary Fragment"
    }
    val text: LiveData<String> = _text
}