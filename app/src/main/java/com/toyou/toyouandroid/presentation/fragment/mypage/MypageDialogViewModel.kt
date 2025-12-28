package com.toyou.toyouandroid.presentation.fragment.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.toyou.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MypageDialogViewModel @Inject constructor() : MviViewModel<MypageDialogUiState, MypageDialogEvent, MypageDialogAction>(
    MypageDialogUiState()
) {
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

    init {
        state.onEach { uiState ->
            _title.value = uiState.title
            _subTitle.value = uiState.subTitle
            _leftButtonText.value = uiState.leftButtonText
            _rightButtonText.value = uiState.rightButtonText
            _leftButtonTextColor.value = uiState.leftButtonTextColor
            _rightButtonTextColor.value = uiState.rightButtonTextColor
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: MypageDialogAction) {
        when (action) {
            is MypageDialogAction.SetDialogData -> performSetDialogData(
                action.title,
                action.subTitle,
                action.leftButtonText,
                action.rightButtonText,
                action.leftButtonTextColor,
                action.rightButtonTextColor,
                action.leftButtonClickAction,
                action.rightButtonClickAction
            )
            is MypageDialogAction.LeftButtonClick -> performLeftButtonClick()
            is MypageDialogAction.RightButtonClick -> performRightButtonClick()
        }
    }

    private fun performSetDialogData(
        title: String,
        subTitle: String?,
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
                subTitle = subTitle,
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
        sendEvent(MypageDialogEvent.LeftButtonClicked)
    }

    private fun performRightButtonClick() {
        currentState.rightButtonClickAction?.invoke()
        sendEvent(MypageDialogEvent.RightButtonClicked)
    }

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
        onAction(
            MypageDialogAction.SetDialogData(
                title,
                subTitle,
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
        onAction(MypageDialogAction.LeftButtonClick)
    }

    fun onRightButtonClick() {
        onAction(MypageDialogAction.RightButtonClick)
    }
}
