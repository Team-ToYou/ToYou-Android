package com.toyou.toyouandroid.presentation.fragment.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarDialogViewModel : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _leftButtonText = MutableLiveData<String>()
    val leftButtonText: LiveData<String> get() = _leftButtonText

    private val _rightButtonText = MutableLiveData<String>()
    val rightButtonText: LiveData<String> get() = _rightButtonText

    private val _leftButtonTextColor = MutableLiveData<Int>()
    val leftButtonTextColor: LiveData<Int> get() = _leftButtonTextColor

    private val _rightButtonTextColor = MutableLiveData<Int>()
    val rightButtonTextColor: LiveData<Int> get() = _rightButtonTextColor

    private val _leftButtonClickAction = MutableLiveData<() -> Unit>()
    private val _rightButtonClickAction = MutableLiveData<() -> Unit>()

    fun setDialogData(
        title: String,
        leftButtonText: String,
        rightButtonText: String,
        leftButtonTextColor: Int,
        rightButtonTextColor: Int,
        leftButtonClickAction: () -> Unit,
        rightButtonClickAction: () -> Unit
    ) {
        _title.value = title
        _leftButtonText.value = leftButtonText
        _rightButtonText.value = rightButtonText
        _leftButtonTextColor.value = leftButtonTextColor
        _rightButtonTextColor.value = rightButtonTextColor
        _leftButtonClickAction.value = leftButtonClickAction
        _rightButtonClickAction.value = rightButtonClickAction
    }

    fun onLeftButtonClick() {
        _leftButtonClickAction.value?.invoke()
    }

    fun onRightButtonClick() {
        _rightButtonClickAction.value?.invoke()
    }
}