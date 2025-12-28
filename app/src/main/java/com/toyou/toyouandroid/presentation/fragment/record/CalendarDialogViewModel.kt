package com.toyou.toyouandroid.presentation.fragment.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CalendarDialogViewModel @Inject constructor() : MviViewModel<CalendarDialogUiState, CalendarDialogEvent, CalendarDialogAction>(
    CalendarDialogUiState()
) {
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

    init {
        state.onEach { uiState ->
            _title.value = uiState.title
            _leftButtonText.value = uiState.leftButtonText
            _rightButtonText.value = uiState.rightButtonText
            _leftButtonTextColor.value = uiState.leftButtonTextColor
            _rightButtonTextColor.value = uiState.rightButtonTextColor
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: CalendarDialogAction) {
        when (action) {
            is CalendarDialogAction.SetDialogData -> performSetDialogData(
                action.title,
                action.leftButtonText,
                action.rightButtonText,
                action.leftButtonTextColor,
                action.rightButtonTextColor,
                action.leftButtonClickAction,
                action.rightButtonClickAction
            )
            is CalendarDialogAction.LeftButtonClick -> performLeftButtonClick()
            is CalendarDialogAction.RightButtonClick -> performRightButtonClick()
        }
    }

    private fun performSetDialogData(
        title: String,
        leftButtonText: String,
        rightButtonText: String,
        leftButtonTextColor: Int,
        rightButtonTextColor: Int,
        leftButtonClickAction: () -> Unit,
        rightButtonClickAction: () -> Unit
    ) {
        updateState {
            copy(
                title = title,
                leftButtonText = leftButtonText,
                rightButtonText = rightButtonText,
                leftButtonTextColor = leftButtonTextColor,
                rightButtonTextColor = rightButtonTextColor,
                leftButtonClickAction = leftButtonClickAction,
                rightButtonClickAction = rightButtonClickAction
            )
        }
    }

    private fun performLeftButtonClick() {
        currentState.leftButtonClickAction?.invoke()
        sendEvent(CalendarDialogEvent.LeftButtonClicked)
    }

    private fun performRightButtonClick() {
        currentState.rightButtonClickAction?.invoke()
        sendEvent(CalendarDialogEvent.RightButtonClicked)
    }

    fun setDialogData(
        title: String,
        leftButtonText: String,
        rightButtonText: String,
        leftButtonTextColor: Int,
        rightButtonTextColor: Int,
        leftButtonClickAction: () -> Unit,
        rightButtonClickAction: () -> Unit
    ) {
        onAction(
            CalendarDialogAction.SetDialogData(
                title,
                leftButtonText,
                rightButtonText,
                leftButtonTextColor,
                rightButtonTextColor,
                leftButtonClickAction,
                rightButtonClickAction
            )
        )
    }

    fun onLeftButtonClick() {
        onAction(CalendarDialogAction.LeftButtonClick)
    }

    fun onRightButtonClick() {
        onAction(CalendarDialogAction.RightButtonClick)
    }
}
