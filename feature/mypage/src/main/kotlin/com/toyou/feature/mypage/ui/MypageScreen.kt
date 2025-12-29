package com.toyou.feature.mypage.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.mypage.R
import com.toyou.feature.mypage.viewmodel.MypageAction
import com.toyou.feature.mypage.viewmodel.MypageEvent
import com.toyou.feature.mypage.viewmodel.MypageViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MypageScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNoticeSetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MypageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onAction(MypageAction.LoadMypage)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is MypageEvent.LogoutResult -> {
                    if (event.success) {
                        Toast.makeText(context, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                        onNavigateToLogin()
                    }
                }
                is MypageEvent.SignOutResult -> {
                    if (event.success) {
                        Toast.makeText(context, "회원탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show()
                        onNavigateToLogin()
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.onAction(MypageAction.Logout)
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showSignOutDialog) {
        SignOutDialog(
            onConfirm = {
                showSignOutDialog = false
                viewModel.onAction(MypageAction.SignOut)
            },
            onDismiss = { showSignOutDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        MypageHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Profile section
        ProfileSection(
            nickname = uiState.nickname ?: "",
            friendCount = uiState.friendNum ?: 0,
            isLoading = uiState.isLoading,
            onProfileClick = onNavigateToProfile
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 8.dp)

        // Menu items
        MypageMenuItem(
            title = stringResource(R.string.mypage_notice_setting),
            onClick = onNavigateToNoticeSetting
        )

        MypageMenuItem(
            title = stringResource(R.string.mypage_terms_of_use),
            onClick = { /* TODO: Navigate to terms */ }
        )

        MypageMenuItem(
            title = stringResource(R.string.mypage_version),
            subtitle = stringResource(R.string.mypage_version_name),
            showArrow = false,
            onClick = { }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.mypage_logout),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.clickable { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.width(24.dp))

            Text(
                text = stringResource(R.string.mypage_signout),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.clickable { showSignOutDialog = true }
            )
        }
    }
}

@Composable
private fun MypageHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.mypage_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
    }
}

@Composable
private fun ProfileSection(
    nickname: String,
    friendCount: Int,
    isLoading: Boolean,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProfileClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = nickname.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nickname.ifEmpty { "로딩 중..." },
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "친구 ${friendCount}명",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to profile",
            tint = Color.Gray
        )
    }
}

@Composable
private fun MypageMenuItem(
    title: String,
    subtitle: String? = null,
    showArrow: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "로그아웃",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(text = "정말 로그아웃 하시겠습니까?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "확인",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = Color.Gray
                )
            }
        }
    )
}

@Composable
private fun SignOutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "회원탈퇴",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(text = "정말 탈퇴하시겠습니까?\n모든 데이터가 삭제됩니다.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "탈퇴",
                    color = Color.Red
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = Color.Gray
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MypageScreenPreview() {
    ToYouTheme {
        MypageScreen(
            onNavigateToLogin = {},
            onNavigateToProfile = {},
            onNavigateToNoticeSetting = {}
        )
    }
}
