package com.toyou.toyouandroid.presentation.fragment.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoticeDialogViewModel @Inject constructor() : MviViewModel<NoticeDialogUiState, NoticeDialogEvent, NoticeDialogAction>(
    NoticeDialogUiState()
) {
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _leftButtonText = MutableLiveData<String>()
    val leftButtonText: LiveData<String> get() = _leftButtonText

    init {
        state.onEach { uiState ->
            _title.value = uiState.title
            _leftButtonText.value = uiState.leftButtonText
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: NoticeDialogAction) {
        when (action) {
            is NoticeDialogAction.SetDialogData -> performSetDialogData(
                action.title,
                action.leftButtonText,
                action.leftButtonClickAction
            )
            is NoticeDialogAction.LeftButtonClick -> performLeftButtonClick()
        }
    }

    private fun performSetDialogData(
        title: String,
        leftButtonText: String,
        leftButtonClickAction: () -> Unit
    ) {
        updateState {
            copy(
                title = title,
                leftButtonText = leftButtonText,
                leftButtonClickAction = leftButtonClickAction
            )
        }
    }

    private fun performLeftButtonClick() {
        currentState.leftButtonClickAction?.invoke()
        sendEvent(NoticeDialogEvent.LeftButtonClicked)
    }

    fun setDialogData(
        title: String,
        leftButtonText: String,
        leftButtonClickAction: () -> Unit
    ) {
        onAction(
            NoticeDialogAction.SetDialogData(
                title,
                leftButtonText,
                leftButtonClickAction
            )
        )
    }

    fun onLeftButtonClick() {
        onAction(NoticeDialogAction.LeftButtonClick)
    }
}
