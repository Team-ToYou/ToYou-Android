package com.toyou.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * MVI 패턴을 위한 Base ViewModel
 *
 * @param S UI 상태 타입 (UiState 구현체)
 * @param E 이벤트 타입 (UiEvent 구현체) - Navigation, Snackbar 등 일회성
 * @param A 액션 타입 (UiAction 구현체) - 사용자 인텐트
 *
 * Usage:
 * ```
 * class HomeViewModel : MviViewModel<HomeState, HomeEvent, HomeAction>(HomeState()) {
 *     override fun handleAction(action: HomeAction) {
 *         when (action) {
 *             is HomeAction.LoadData -> loadData()
 *             is HomeAction.Refresh -> refresh()
 *         }
 *     }
 * }
 * ```
 */
abstract class MviViewModel<S : UiState, E : UiEvent, A : UiAction>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _event = Channel<E>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    /**
     * 현재 상태를 반환
     */
    protected val currentState: S
        get() = _state.value

    /**
     * 상태 업데이트
     * @param reduce 현재 상태를 받아 새 상태를 반환하는 함수
     */
    protected fun updateState(reduce: S.() -> S) {
        _state.value = currentState.reduce()
    }

    /**
     * 일회성 이벤트 발행
     * Navigation, Snackbar, Toast 등에 사용
     */
    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    /**
     * 사용자 액션 처리
     * View에서 호출하여 인텐트 전달
     */
    fun onAction(action: A) {
        handleAction(action)
    }

    /**
     * 액션 처리 로직 구현
     * 하위 클래스에서 when 문으로 각 액션 처리
     */
    protected abstract fun handleAction(action: A)
}
