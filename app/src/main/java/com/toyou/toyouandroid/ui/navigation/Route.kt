package com.toyou.toyouandroid.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization
 */
sealed interface Route {
    // Onboarding
    @Serializable
    data object Splash : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object SignupAgree : Route

    @Serializable
    data object SignupNickname : Route

    @Serializable
    data object SignupStatus : Route

    @Serializable
    data object Tutorial : Route

    // Main (Bottom Navigation)
    @Serializable
    data object Home : Route

    @Serializable
    data object Social : Route

    @Serializable
    data object Record : Route

    @Serializable
    data object Mypage : Route

    // Home Flow
    @Serializable
    data object HomeOption : Route

    @Serializable
    data object HomeResult : Route

    @Serializable
    data object Create : Route

    @Serializable
    data object CreateWrite : Route

    @Serializable
    data object Preview : Route

    @Serializable
    data object Modify : Route

    @Serializable
    data object Notice : Route

    // Social Flow
    @Serializable
    data object QuestionType : Route

    @Serializable
    data object QuestionContent : Route

    @Serializable
    data object QuestionContentLong : Route

    @Serializable
    data object Send : Route

    @Serializable
    data object SendFinal : Route

    // Record Flow
    @Serializable
    data object MyCardContainer : Route

    @Serializable
    data object FriendCardContainer : Route

    @Serializable
    data class FriendCardDetail(val cardId: Long) : Route

    // Mypage Flow
    @Serializable
    data object Profile : Route

    @Serializable
    data object NoticeSetting : Route
}

/**
 * Bottom navigation items
 */
val bottomNavRoutes = listOf(
    Route.Home,
    Route.Social,
    Route.Record,
    Route.Mypage
)
