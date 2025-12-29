package com.toyou.feature.notice.screen

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.notice.viewmodel.NoticeAction
import com.toyou.feature.notice.viewmodel.NoticeEvent
import com.toyou.feature.notice.viewmodel.NoticeItem
import com.toyou.feature.notice.viewmodel.NoticeViewModel
import com.toyou.feature.social.viewmodel.SocialAction
import com.toyou.feature.social.viewmodel.SocialViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NoticeScreen(
    onBackClick: () -> Unit,
    onNavigateToSocial: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToModify: () -> Unit,
    modifier: Modifier = Modifier,
    noticeViewModel: NoticeViewModel = hiltViewModel(),
    socialViewModel: SocialViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by noticeViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        noticeViewModel.onAction(NoticeAction.FetchNotices)
        noticeViewModel.onAction(NoticeAction.FetchFriendsRequestNotices)
    }

    LaunchedEffect(Unit) {
        noticeViewModel.event.collectLatest { event ->
            when (event) {
                is NoticeEvent.NoticeDeleted -> {
                    Toast.makeText(context, "알림이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                }
                is NoticeEvent.NoticeDeleteFailed -> {
                    Toast.makeText(context, "알림 삭제에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
                is NoticeEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is NoticeEvent.TokenExpired -> {
                    // Handle token expiration
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToYouTopAppBar(
            title = "알림",
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.generalNotices.isEmpty() && uiState.friendRequestNotices.isEmpty()) {
            EmptyNoticeContent()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // Friend request section
                if (uiState.friendRequestNotices.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "친구 요청",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    itemsIndexed(uiState.friendRequestNotices) { index, item ->
                        FriendRequestNoticeItem(
                            item = item,
                            onAccept = { nickname ->
                                socialViewModel.onAction(
                                    SocialAction.ApproveNotice(
                                        name = nickname,
                                        myName = "", // Will be fetched from user state
                                        alarmId = item.alarmId,
                                        position = index
                                    )
                                )
                            },
                            onDelete = {
                                noticeViewModel.onAction(
                                    NoticeAction.DeleteNotice(item.alarmId, index)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // General notices section
                if (uiState.generalNotices.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "알림",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    itemsIndexed(uiState.generalNotices) { index, item ->
                        GeneralNoticeItem(
                            item = item,
                            onClick = {
                                when (item) {
                                    is NoticeItem.NoticeCardCheckItem -> onNavigateToCreate()
                                    is NoticeItem.NoticeFriendRequestAcceptedItem -> onNavigateToSocial()
                                    else -> {}
                                }
                            },
                            onDelete = {
                                noticeViewModel.onAction(
                                    NoticeAction.DeleteNotice(item.alarmId, index)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun FriendRequestNoticeItem(
    item: NoticeItem.NoticeFriendRequestItem,
    onAccept: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${item.nickname}님이 친구 요청을 보냈습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onAccept(item.nickname) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "수락",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun GeneralNoticeItem(
    item: NoticeItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = when (item) {
        is NoticeItem.NoticeFriendRequestAcceptedItem -> "${item.nickname}님이 친구 요청을 수락했습니다"
        is NoticeItem.NoticeCardCheckItem -> "${item.nickname}님이 질문을 보냈습니다"
        else -> ""
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyNoticeContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "알림이 없습니다",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticeScreenPreview() {
    ToYouTheme {
        NoticeScreen(
            onBackClick = {},
            onNavigateToSocial = {},
            onNavigateToCreate = {},
            onNavigateToModify = {}
        )
    }
}
