package com.toyou.feature.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.onboarding.R
import com.toyou.core.network.model.onboarding.SignUpRequest
import com.toyou.core.domain.model.StatusType
import com.toyou.feature.onboarding.viewmodel.LoginEvent
import com.toyou.feature.onboarding.viewmodel.LoginViewModel
import com.toyou.feature.onboarding.viewmodel.SignupNicknameViewModel
import com.toyou.feature.onboarding.viewmodel.SignupStatusViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupStatusScreen(
    onBackClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    statusViewModel: SignupStatusViewModel = hiltViewModel(),
    nicknameViewModel: SignupNicknameViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by statusViewModel.state.collectAsState()
    val nicknameState by nicknameViewModel.state.collectAsState()
    val loginState by loginViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        loginViewModel.event.collectLatest { event ->
            when (event) {
                is LoginEvent.SignUpSucceeded -> onCompleteClick()
                is LoginEvent.SignUpFailed -> {
                    // Handle signup failure - could show error message
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToYouTopAppBar(
            title = stringResource(R.string.signup_screen_bar_title),
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.signup_status_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.signup_status_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusOption(
                    text = stringResource(R.string.signup_status_option_1),
                    isSelected = uiState.selectedStatusType == StatusType.SCHOOL,
                    onClick = { statusViewModel.onStatusSelected(StatusType.SCHOOL) }
                )

                StatusOption(
                    text = stringResource(R.string.signup_status_option_2),
                    isSelected = uiState.selectedStatusType == StatusType.COLLEGE,
                    onClick = { statusViewModel.onStatusSelected(StatusType.COLLEGE) }
                )

                StatusOption(
                    text = stringResource(R.string.signup_status_option_3),
                    isSelected = uiState.selectedStatusType == StatusType.OFFICE,
                    onClick = { statusViewModel.onStatusSelected(StatusType.OFFICE) }
                )

                StatusOption(
                    text = stringResource(R.string.signup_status_option_4),
                    isSelected = uiState.selectedStatusType == StatusType.ETC,
                    onClick = { statusViewModel.onStatusSelected(StatusType.ETC) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ToYouButton(
                text = stringResource(R.string.complete_button),
                onClick = {
                    val signUpRequest = SignUpRequest(
                        adConsent = true,
                        nickname = nicknameState.nickname,
                        status = uiState.status
                    )
                    loginViewModel.signUp(signUpRequest)
                },
                enabled = uiState.isNextButtonEnabled,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun StatusOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignupStatusScreenPreview() {
    ToYouTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            ToYouTopAppBar(
                title = "회원가입",
                onNavigationClick = {}
            )

            HorizontalDivider(color = Color(0xFFF5F5F5))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "현재 상태를 알려주세요.",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "선택하신 정보를 기반으로 맞춤형 질문을 추천해드립니다.\n다른 목적으로 사용되거나 제 3자에게 제공되지 않습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusOption(
                        text = "중·고등학생",
                        isSelected = true,
                        onClick = {}
                    )

                    StatusOption(
                        text = "대학생",
                        isSelected = false,
                        onClick = {}
                    )

                    StatusOption(
                        text = "직장인",
                        isSelected = false,
                        onClick = {}
                    )

                    StatusOption(
                        text = "기타",
                        isSelected = false,
                        onClick = {}
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                ToYouButton(
                    text = "완료",
                    onClick = {},
                    enabled = true,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
        }
    }
}
