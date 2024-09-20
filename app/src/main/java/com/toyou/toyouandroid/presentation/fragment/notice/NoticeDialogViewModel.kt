package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoticeDialogViewModel : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _leftButtonText = MutableLiveData<String>()
    val leftButtonText: LiveData<String> get() = _leftButtonText

    private val _leftButtonClickAction = MutableLiveData<() -> Unit>()

    fun setDialogData(
        title: String,
        leftButtonText: String,
        leftButtonClickAction: () -> Unit,
    ) {
        _title.value = title
        _leftButtonText.value = leftButtonText
        _leftButtonClickAction.value = leftButtonClickAction
    }

    fun onLeftButtonClick() {
        _leftButtonClickAction.value?.invoke()
    }
}