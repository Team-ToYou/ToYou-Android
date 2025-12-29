package com.toyou.feature.mypage.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
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
import com.toyou.feature.mypage.R
import com.toyou.core.domain.model.DuplicateCheckMessageType
import com.toyou.core.domain.model.StatusType
import com.toyou.feature.mypage.viewmodel.ProfileAction
import com.toyou.feature.mypage.viewmodel.ProfileEvent
import com.toyou.feature.mypage.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ProfileEvent.NicknameChangedSuccess -> {
                    Toast.makeText(context, "프로필이 변경되었습니다", Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
                is ProfileEvent.DuplicateCheckMessageChanged -> {
                    // Handled in UI state
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
                interactionSource = remember { MutableInteractionSource() }
            ) { focusManager.clearFocus() }
    ) {
        ToYouTopAppBar(
            title = "프로필 수정",
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Nickname section
            Text(
                text = stringResource(R.string.profile_nickname_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            NicknameInputField(
                nickname = uiState.nickname,
                textCount = uiState.textCount,
                isDuplicateCheckEnabled = uiState.isDuplicateCheckEnabled,
                onNicknameChange = { newNickname ->
                    viewModel.onAction(ProfileAction.SetNickname(newNickname))
                    viewModel.onAction(ProfileAction.UpdateTextCount(newNickname.length))
                    viewModel.onAction(ProfileAction.DuplicateBtnActivate)
                    viewModel.onAction(ProfileAction.UpdateLength15(newNickname.length))
                },
                onDuplicateCheck = {
                    focusManager.clearFocus()
                    viewModel.onAction(ProfileAction.CheckDuplicate(uiState.nickname, 1))
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.duplicateCheckMessage,
                style = MaterialTheme.typography.bodySmall,
                color = getDuplicateCheckMessageColor(uiState.duplicateCheckMessage),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Status section
            Text(
                text = "현재 상태",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatusSelector(
                selectedStatus = uiState.selectedStatusType,
                onStatusSelected = { statusType ->
                    viewModel.onAction(ProfileAction.OnStatusButtonClicked(statusType))
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            ToYouButton(
                text = "저장",
                onClick = {
                    viewModel.onAction(ProfileAction.ChangeNickname)
                    viewModel.onAction(ProfileAction.ChangeStatus)
                },
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
private fun StatusSelector(
    selectedStatus: StatusType?,
    onStatusSelected: (StatusType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusButton(
                text = stringResource(R.string.signup_status_option_1),
                isSelected = selectedStatus == StatusType.SCHOOL,
                onClick = { onStatusSelected(StatusType.SCHOOL) },
                modifier = Modifier.weight(1f)
            )
            StatusButton(
                text = stringResource(R.string.signup_status_option_2),
                isSelected = selectedStatus == StatusType.COLLEGE,
                onClick = { onStatusSelected(StatusType.COLLEGE) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusButton(
                text = stringResource(R.string.signup_status_option_3),
                isSelected = selectedStatus == StatusType.OFFICE,
                onClick = { onStatusSelected(StatusType.OFFICE) },
                modifier = Modifier.weight(1f)
            )
            StatusButton(
                text = stringResource(R.string.signup_status_option_4),
                isSelected = selectedStatus == StatusType.ETC,
                onClick = { onStatusSelected(StatusType.ETC) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color(0xFFF5F5F5)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

private fun getDuplicateCheckMessageColor(message: String): Color {
    return when {
        message.contains("사용 가능") -> Color(0xFFEA9797)
        message.contains("확인해주세요") -> Color.Gray
        else -> Color.Red
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ToYouTheme {
        ProfileScreen(
            onBackClick = {}
        )
    }
}
