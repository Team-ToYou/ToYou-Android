package com.toyou.core.common.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Fragment/Activity에서 StateFlow를 안전하게 수집
 *
 * Usage:
 * ```
 * viewLifecycleOwner.collectState(viewModel.state) { state ->
 *     // UI 업데이트
 * }
 * ```
 */
fun <T> LifecycleOwner.collectState(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            flow.collect(collector)
        }
    }
}

/**
 * Fragment/Activity에서 Event를 안전하게 수집
 *
 * Usage:
 * ```
 * viewLifecycleOwner.collectEvent(viewModel.event) { event ->
 *     when (event) {
 *         is HomeEvent.NavigateToDetail -> navigateToDetail()
 *         is HomeEvent.ShowError -> showError(event.message)
 *     }
 * }
 * ```
 */
fun <T> LifecycleOwner.collectEvent(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            flow.collect(collector)
        }
    }
}
