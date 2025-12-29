package com.toyou.core.data.utils

import com.toyou.core.datastore.TokenManager
import com.toyou.core.domain.model.DomainResult
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API 에러 처리를 위한 유틸리티 클래스
 *
 * 토큰 갱신이 필요한 경우 자동으로 처리하고 재시도합니다.
 */
@Singleton
class ApiErrorHandler @Inject constructor(
    private val tokenManager: TokenManager
) {
    /**
     * DomainResult.Error를 처리하고 필요시 토큰을 갱신한 후 재시도합니다.
     *
     * @param error DomainResult.Error 인스턴스
     * @param onRetry 토큰 갱신 성공 시 재시도할 작업
     * @param onFailure 토큰 갱신 실패 시 실행할 작업
     * @param tag 로깅에 사용할 태그 (기본값: "ApiErrorHandler")
     */
    fun handleError(
        error: DomainResult.Error,
        onRetry: () -> Unit,
        onFailure: () -> Unit,
        tag: String = "ApiErrorHandler"
    ) {
        Timber.tag(tag).d("API 실패: ${error.message}")
        tokenManager.refreshToken(
            onSuccess = {
                Timber.tag(tag).d("토큰 갱신 성공, 재시도 중...")
                onRetry()
            },
            onFailure = {
                Timber.tag(tag).e("토큰 갱신 실패")
                onFailure()
            }
        )
    }

    /**
     * 401 에러 응답을 처리하고 토큰을 갱신한 후 재시도합니다.
     *
     * @param responseCode HTTP 응답 코드
     * @param onRetry 토큰 갱신 성공 시 재시도할 작업
     * @param onFailure 토큰 갱신 실패 시 실행할 작업
     * @param tag 로깅에 사용할 태그 (기본값: "ApiErrorHandler")
     * @return true if error was handled (401), false otherwise
     */
    fun handleUnauthorized(
        responseCode: Int,
        onRetry: () -> Unit,
        onFailure: () -> Unit,
        tag: String = "ApiErrorHandler"
    ): Boolean {
        if (responseCode == 401) {
            Timber.tag(tag).d("401 Unauthorized - 토큰 갱신 시도 중...")
            tokenManager.refreshToken(
                onSuccess = {
                    Timber.tag(tag).d("토큰 갱신 성공, 재시도 중...")
                    onRetry()
                },
                onFailure = {
                    Timber.tag(tag).e("토큰 갱신 실패")
                    onFailure()
                }
            )
            return true
        }
        return false
    }

    /**
     * 재시도 로직이 포함된 DomainResult 처리
     *
     * @param maxRetries 최대 재시도 횟수 (기본값: 5)
     * @param currentRetry 현재 재시도 횟수
     * @param error DomainResult.Error 인스턴스
     * @param onRetry 재시도할 작업 (재시도 횟수를 인자로 받음)
     * @param onMaxRetriesExceeded 최대 재시도 횟수 초과 시 실행할 작업
     * @param tag 로깅에 사용할 태그
     */
    fun handleErrorWithRetry(
        maxRetries: Int = 5,
        currentRetry: Int,
        error: DomainResult.Error,
        onRetry: (Int) -> Unit,
        onMaxRetriesExceeded: () -> Unit,
        tag: String = "ApiErrorHandler"
    ) {
        if (currentRetry < maxRetries) {
            Timber.tag(tag).d("재시도 중... ($currentRetry/$maxRetries)")
            tokenManager.refreshToken(
                onSuccess = {
                    Timber.tag(tag).d("토큰 갱신 성공, 재시도 중...")
                    onRetry(currentRetry + 1)
                },
                onFailure = {
                    Timber.tag(tag).e("토큰 갱신 실패 (재시도 $currentRetry/$maxRetries)")
                    onMaxRetriesExceeded()
                }
            )
        } else {
            Timber.tag(tag).e("최대 재시도 횟수 도달 ($maxRetries)")
            onMaxRetriesExceeded()
        }
    }
}
