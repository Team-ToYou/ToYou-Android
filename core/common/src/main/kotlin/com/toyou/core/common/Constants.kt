package com.toyou.core.common

/**
 * 프로젝트 전체에서 사용되는 상수 정의
 */
object AppConstants {
    /**
     * 네트워크 관련 상수
     */
    object Network {
        const val BASE_URL = "https://to-you.shop"
        const val MAX_RETRY_COUNT = 2
        const val HEADER_RETRY_COUNT = "Retry-Count"
    }

    /**
     * FCM 관련 상수
     */
    object Fcm {
        const val TOPIC_ALL_USERS = "allUsers"
        const val CHANNEL_ID = "FCM__channel_id"
        const val CHANNEL_NAME = "FCM STUDY"
    }

    /**
     * UI 관련 상수
     */
    object Ui {
        const val DEFAULT_EMOTION_TEXT = "멘트"
    }
}
