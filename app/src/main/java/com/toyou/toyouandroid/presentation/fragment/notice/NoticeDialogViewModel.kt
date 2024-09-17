package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoticeDialogViewModel : ViewModel() {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _subTitle = MutableLiveData<String?>()
    val subTitle: LiveData<String?> get() = _subTitle

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
        subTitle: String?,
        leftButtonText: String,
        rightButtonText: String,
        leftButtonTextColor: Int,
        rightButtonTextColor: Int,
        leftButtonClickAction: () -> Unit,
        rightButtonClickAction: () -> Unit
    ) {
        _title.value = title
        _subTitle.value = subTitle
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