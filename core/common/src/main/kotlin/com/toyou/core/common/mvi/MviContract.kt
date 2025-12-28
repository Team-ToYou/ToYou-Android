package com.toyou.core.common.mvi

/**
 * MVI 패턴의 기본 계약 인터페이스들
 *
 * State: 화면의 현재 상태 (지속적)
 * Event: 일회성 부수효과 (Navigation, Snackbar 등)
 * Action: 사용자 인텐트/액션
 */

/** UI 상태를 나타내는 마커 인터페이스 */
interface UiState

/** 일회성 이벤트(Side Effect)를 나타내는 마커 인터페이스 */
interface UiEvent

/** 사용자 액션/인텐트를 나타내는 마커 인터페이스 */
interface UiAction
