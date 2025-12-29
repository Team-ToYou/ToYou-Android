package com.toyou.toyouandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.toyou.feature.create.screen.CreateScreen
import com.toyou.feature.create.screen.CreateWriteScreen
import com.toyou.feature.create.screen.ModifyScreen
import com.toyou.feature.create.screen.PreviewScreen
import com.toyou.feature.home.screen.HomeOptionScreen
import com.toyou.feature.home.screen.HomeResultScreen
import com.toyou.feature.home.screen.HomeScreen
import com.toyou.feature.mypage.screen.MypageScreen
import com.toyou.feature.mypage.screen.NoticeSettingScreen
import com.toyou.feature.mypage.screen.ProfileScreen
import com.toyou.feature.notice.screen.NoticeScreen
import com.toyou.feature.onboarding.screen.LoginScreen
import com.toyou.feature.onboarding.screen.SignupAgreeScreen
import com.toyou.feature.onboarding.screen.SignupNicknameScreen
import com.toyou.feature.onboarding.screen.SignupStatusScreen
import com.toyou.feature.onboarding.screen.SplashScreen
import com.toyou.feature.onboarding.screen.TutorialScreen
import com.toyou.feature.record.screen.FriendCardContainerScreen
import com.toyou.feature.record.screen.MyCardContainerScreen
import com.toyou.feature.record.screen.RecordScreen
import com.toyou.feature.social.screen.QuestionContentLongScreen
import com.toyou.feature.social.screen.QuestionContentScreen
import com.toyou.feature.social.screen.QuestionTypeScreen
import com.toyou.feature.social.screen.SendFinalScreen
import com.toyou.feature.social.screen.SendScreen
import com.toyou.feature.social.screen.SocialScreen

@Composable
fun ToYouNavHost(
    navController: NavHostController,
    onShowBottomBar: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash,
        modifier = modifier
    ) {
        // Onboarding Flow
        composable<Route.Splash> {
            onShowBottomBar(false)
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo<Route.Splash> { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Login> {
            onShowBottomBar(false)
            LoginScreen(
                onNavigateToSignupAgree = {
                    navController.navigate(Route.SignupAgree)
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                },
                onNavigateToTutorial = {
                    navController.navigate(Route.Tutorial) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.SignupAgree> {
            onShowBottomBar(false)
            SignupAgreeScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { navController.navigate(Route.SignupNickname) }
            )
        }

        composable<Route.SignupNickname> {
            onShowBottomBar(false)
            SignupNicknameScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { navController.navigate(Route.SignupStatus) }
            )
        }

        composable<Route.SignupStatus> {
            onShowBottomBar(false)
            SignupStatusScreen(
                onBackClick = { navController.popBackStack() },
                onCompleteClick = {
                    navController.navigate(Route.Tutorial) {
                        popUpTo<Route.Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Tutorial> {
            onShowBottomBar(false)
            TutorialScreen(
                onComplete = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Tutorial> { inclusive = true }
                    }
                }
            )
        }

        // Main Screens (Bottom Navigation)
        composable<Route.Home> {
            onShowBottomBar(true)
            HomeScreen(
                onNavigateToCreate = { navController.navigate(Route.Create) },
                onNavigateToNotice = { navController.navigate(Route.Notice) },
                onNavigateToHomeOption = { navController.navigate(Route.HomeOption) },
                onNavigateToModify = { navController.navigate(Route.Modify) },
                onNavigateToFriendCard = { cardId ->
                    navController.navigate(Route.FriendCardDetail(cardId))
                }
            )
        }

        composable<Route.Social> {
            onShowBottomBar(true)
            SocialScreen(
                onNavigateToQuestionType = { navController.navigate(Route.QuestionType) }
            )
        }

        composable<Route.Record> {
            onShowBottomBar(true)
            RecordScreen(
                onNavigateToMyCard = { navController.navigate(Route.MyCardContainer) },
                onNavigateToFriendCard = { navController.navigate(Route.FriendCardContainer) }
            )
        }

        composable<Route.Mypage> {
            onShowBottomBar(true)
            MypageScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateToProfile = { navController.navigate(Route.Profile) },
                onNavigateToNoticeSetting = { navController.navigate(Route.NoticeSetting) }
            )
        }

        // Home Flow
        composable<Route.HomeOption> {
            onShowBottomBar(false)
            HomeOptionScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToResult = { navController.navigate(Route.HomeResult) }
            )
        }

        composable<Route.HomeResult> {
            onShowBottomBar(false)
            HomeResultScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Home> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Create> {
            onShowBottomBar(false)
            CreateScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToCreateWrite = { navController.navigate(Route.CreateWrite) }
            )
        }

        composable<Route.CreateWrite> {
            onShowBottomBar(false)
            CreateWriteScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToPreview = { navController.navigate(Route.Preview) }
            )
        }

        composable<Route.Preview> {
            onShowBottomBar(false)
            PreviewScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Home> { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Modify> {
            onShowBottomBar(false)
            ModifyScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate(Route.Create) }
            )
        }

        composable<Route.Notice> {
            onShowBottomBar(false)
            NoticeScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSocial = { navController.navigate(Route.Social) },
                onNavigateToCreate = { navController.navigate(Route.Create) },
                onNavigateToModify = { navController.navigate(Route.Modify) }
            )
        }

        // Social Flow
        composable<Route.QuestionType> {
            onShowBottomBar(false)
            QuestionTypeScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToContent = { navController.navigate(Route.QuestionContent) },
                onNavigateToContentLong = { navController.navigate(Route.QuestionContentLong) }
            )
        }

        composable<Route.QuestionContent> {
            onShowBottomBar(false)
            QuestionContentScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSend = { navController.navigate(Route.Send) }
            )
        }

        composable<Route.QuestionContentLong> {
            onShowBottomBar(false)
            QuestionContentLongScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSend = { navController.navigate(Route.Send) }
            )
        }

        composable<Route.Send> {
            onShowBottomBar(false)
            SendScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSendFinal = { navController.navigate(Route.SendFinal) }
            )
        }

        composable<Route.SendFinal> {
            onShowBottomBar(false)
            SendFinalScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Home> { inclusive = true }
                    }
                }
            )
        }

        // Record Flow
        composable<Route.MyCardContainer> {
            onShowBottomBar(false)
            MyCardContainerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.FriendCardContainer> {
            onShowBottomBar(false)
            FriendCardContainerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.FriendCardDetail> { backStackEntry ->
            val route: Route.FriendCardDetail = backStackEntry.toRoute()
            onShowBottomBar(false)
            FriendCardContainerScreen(
                cardId = route.cardId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Mypage Flow
        composable<Route.Profile> {
            onShowBottomBar(false)
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Route.NoticeSetting> {
            onShowBottomBar(false)
            NoticeSettingScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
