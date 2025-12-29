package com.toyou.feature.social.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.home.R
import com.toyou.core.domain.model.FriendListModel
import com.toyou.feature.social.viewmodel.SocialAction
import com.toyou.feature.social.viewmodel.SocialViewModel

@Composable
fun SocialScreen(
    onNavigateToQuestionType: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onAction(SocialAction.LoadFriends)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        SocialHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "친구에게 질문을 보내보세요!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "친구의 일기카드 작성을 도와줄 수 있어요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Friends section
        Text(
            text = "내 친구",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (uiState.friends.isEmpty()) {
            EmptyFriendsContent()
        } else {
            FriendsList(
                friends = uiState.friends,
                selectedIndex = uiState.selectedChar,
                onFriendClick = { index, friend ->
                    viewModel.onAction(SocialAction.SelectCharacter(index))
                    viewModel.onAction(
                        SocialAction.SetTargetFriend(
                            friendName = friend.name,
                            emotion = friend.emotion,
                            ment = friend.message
                        )
                    )
                    onNavigateToQuestionType()
                }
            )
        }
    }
}

@Composable
private fun SocialHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "투유",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    }
}

@Composable
private fun FriendsList(
    friends: List<FriendListModel>,
    selectedIndex: Int,
    onFriendClick: (Int, FriendListModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(friends) { index, friend ->
            FriendItem(
                friend = friend,
                isSelected = index == selectedIndex,
                onClick = { onFriendClick(index, friend) }
            )
        }
    }
}

@Composable
private fun FriendItem(
    friend: FriendListModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else Color(0xFFF5F5F5)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getCharacterDrawable(friend.emotion)),
                contentDescription = friend.name,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = friend.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}

@Composable
private fun EmptyFriendsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_bottomsheet_pseudo),
            contentDescription = "No friends",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "아직 친구가 없어요\n친구를 추가해보세요!",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

private fun getCharacterDrawable(emotion: Int?): Int {
    return when (emotion) {
        0 -> R.drawable.home_emotion_happy
        1 -> R.drawable.home_emotion_exciting
        2 -> R.drawable.home_emotion_normal
        3 -> R.drawable.home_emotion_anxiety
        4 -> R.drawable.home_emotion_upset
        else -> R.drawable.home_emotion_none
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialScreenPreview() {
    ToYouTheme {
        EmptyFriendsContent()
    }
}
