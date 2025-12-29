package com.toyou.feature.onboarding.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.onboarding.R
import com.toyou.core.domain.model.DuplicateCheckMessageType
import com.toyou.feature.onboarding.viewmodel.SignupNicknameEvent
import com.toyou.feature.onboarding.viewmodel.SignupNicknameViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignupNicknameScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupNicknameViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is SignupNicknameEvent.ServerError -> {
                    // Handle server error - could show a snackbar
                }
                is SignupNicknameEvent.ShowError -> {
                    // Handle error message
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { focusManager.clearFocus() }
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
                text = stringResource(R.string.signup_nickname_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            NicknameInputField(
                nickname = uiState.nickname,
                textCount = uiState.textCount,
                isDuplicateCheckEnabled = uiState.isDuplicateCheckEnabled,
                onNicknameChange = { newNickname ->
                    viewModel.setNickname(newNickname)
                    viewModel.updateTextCount(newNickname.length)
                    viewModel.duplicateBtnActivate()
                    viewModel.updateLength15(newNickname.length)
                },
                onDuplicateCheck = {
                    focusManager.clearFocus()
                    viewModel.checkDuplicate(1)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DuplicateCheckMessage(
                messageType = uiState.duplicateCheckMessageType
            )

            Spacer(modifier = Modifier.weight(1f))

            ToYouButton(
                text = stringResource(R.string.next_button),
                onClick = onNextClick,
                enabled = uiState.isNextButtonEnabled,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun NicknameInputField(
    nickname: String,
    textCount: String,
    isDuplicateCheckEnabled: Boolean,
    onNicknameChange: (String) -> Unit,
    onDuplicateCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = nickname,
                    onValueChange = { if (it.length <= 15) onNicknameChange(it) },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onDuplicateCheck() }),
                    decorationBox = { innerTextField ->
                        Box {
                            if (nickname.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.signup_nickname_placeholder),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Text(
                    text = textCount,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .height(52.dp)
                .background(
                    color = if (isDuplicateCheckEnabled) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = isDuplicateCheckEnabled) { onDuplicateCheck() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.signup_nickname_double_check),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDuplicateCheckEnabled) Color.White else Color.Gray
            )
        }
    }
}

@Composable
private fun DuplicateCheckMessage(
    messageType: DuplicateCheckMessageType,
    modifier: Modifier = Modifier
) {
    val (message, color) = when (messageType) {
        DuplicateCheckMessageType.CHECK_REQUIRED -> messageType.message to Color.Gray
        DuplicateCheckMessageType.LENGTH_EXCEEDED -> messageType.message to Color.Red
        DuplicateCheckMessageType.AVAILABLE -> messageType.message to MaterialTheme.colorScheme.primary
        DuplicateCheckMessageType.ALREADY_IN_USE,
        DuplicateCheckMessageType.ALREADY_IN_USE_SAME -> messageType.message to Color.Red
        DuplicateCheckMessageType.CHECK_FAILED,
        DuplicateCheckMessageType.UPDATE_FAILED,
        DuplicateCheckMessageType.SERVER_ERROR -> messageType.message to Color.Red
    }

    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontSize = 12.sp,
        modifier = modifier.padding(start = 4.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignupNicknameScreenPreview() {
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
                    text = "투유에서 사용할 닉네임을 알려주세요.",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(40.dp))

                NicknameInputField(
                    nickname = "",
                    textCount = "(0/15)",
                    isDuplicateCheckEnabled = false,
                    onNicknameChange = {},
                    onDuplicateCheck = {}
                )

                Spacer(modifier = Modifier.height(8.dp))

                DuplicateCheckMessage(
                    messageType = DuplicateCheckMessageType.CHECK_REQUIRED
                )

                Spacer(modifier = Modifier.weight(1f))

                ToYouButton(
                    text = "다음",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
        }
    }
}
