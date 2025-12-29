package com.toyou.feature.onboarding.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.toyou.core.datastore.TutorialStorage
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.onboarding.R
import com.toyou.feature.onboarding.viewmodel.LoginEvent
import com.toyou.feature.onboarding.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

private val GangwonFontFamily = FontFamily(
    Font(R.font.gangwoneduhyeonokt)
)

private val KakaoYellow = Color(0xFFFEE500)
private val KakaoBlack = Color(0xFF191919)

@Composable
fun LoginScreen(
    onNavigateToSignupAgree: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTutorial: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    tutorialStorage: TutorialStorage? = null
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is LoginEvent.LoginSucceeded -> {
                    viewModel.setLoginSuccess(false)
                    viewModel.setInitialization(false)
                    viewModel.checkIfTokenExists()
                }
                is LoginEvent.LoginFailed -> {
                    Timber.e("Login failed")
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(uiState.checkIfTokenExists, uiState.isInitialization) {
        if (!uiState.isInitialization) {
            if (uiState.checkIfTokenExists) {
                Timber.d("서비스 이용자이므로 튜토리얼 검증")
                val isTutorialShown = tutorialStorage?.isTutorialShown() ?: true
                if (!isTutorialShown) {
                    tutorialStorage?.setTutorialShownSync()
                    onNavigateToTutorial()
                } else {
                    Timber.d("액세스 토큰이 있으므로 홈 화면으로 이동")
                    onNavigateToHome()
                }
                viewModel.setInitialization(true)
                viewModel.setIfTokenExists(false)
            } else if (uiState.loginSuccess) {
                Timber.d("토큰이 존재하지 않으므로 신규 가입")
                viewModel.setInitialization(true)
                onNavigateToSignupAgree()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(0.35f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login_logo),
                    contentDescription = "ToYou Logo"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.ToYou_hanguel),
                    fontFamily = GangwonFontFamily,
                    fontSize = 46.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(0.45f))

            KakaoLoginButton(
                onClick = {
                    performKakaoLogin(context as Activity, viewModel)
                },
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .padding(bottom = 120.dp)
            )
        }
    }
}

private fun performKakaoLogin(activity: Activity, viewModel: LoginViewModel) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Timber.e(error, "카카오계정으로 로그인 실패")
        } else if (token != null) {
            Timber.i("카카오계정으로 로그인 성공 ${token.accessToken}")
            viewModel.setOAuthAccessToken(token.accessToken)
            viewModel.kakaoLogin(token.accessToken)
        }
    }

    if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
        UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
            if (error != null) {
                Timber.e(error, "카카오톡으로 로그인 실패")

                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }

                UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
            } else if (token != null) {
                Timber.i("카카오톡으로 로그인 성공 ${token.accessToken}")
                viewModel.setOAuthAccessToken(token.accessToken)
                viewModel.kakaoLogin(token.accessToken)
            }
        }
    } else {
        UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
    }
}

@Composable
private fun KakaoLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(300.dp)
            .height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KakaoYellow,
            contentColor = KakaoBlack
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.kakao_talk),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "카카오 로그인",
                style = MaterialTheme.typography.labelLarge,
                color = KakaoBlack
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    ToYouTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.weight(0.35f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "친구와 함께 만드는 나만의 일기장",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "투유",
                        fontSize = 46.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(0.45f))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .width(300.dp)
                        .height(40.dp)
                        .padding(bottom = 120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KakaoYellow,
                        contentColor = KakaoBlack
                    )
                ) {
                    Text(
                        text = "카카오 로그인",
                        style = MaterialTheme.typography.labelLarge,
                        color = KakaoBlack
                    )
                }
            }
        }
    }
}
