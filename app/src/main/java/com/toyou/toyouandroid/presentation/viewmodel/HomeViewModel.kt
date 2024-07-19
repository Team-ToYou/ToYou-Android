package com.toyou.toyouandroid.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.toyou.toyouandroid.utils.getCurrentDate

class HomeViewModel : ViewModel() {

    private val _homeEmotion = MutableLiveData<Int>()
    val homeEmotion: LiveData<Int> get() = _homeEmotion

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    private val _homeDateBackground = MutableLiveData<Int>()
    val homeDateBackground: LiveData<Int> get() = _homeDateBackground

    private val _homeBackground = MutableLiveData<Int>()
    val homeBackground: LiveData<Int> get() = _homeBackground

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String> get() = _currentDate

    init {
        _currentDate.value = getCurrentDate()
    }

    fun updateHomeEmotion(emotion: Int, text: String, date: Int, background: Int) {
        _homeEmotion.value = emotion
        _text.value = text
        _homeDateBackground.value = date
        _homeBackground.value = background
    }
}
