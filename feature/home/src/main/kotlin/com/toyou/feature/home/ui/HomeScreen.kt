package com.toyou.feature.home.screen

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toyou.core.designsystem.theme.ToYouTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.feature.home.R
import com.toyou.feature.home.viewmodel.HomeAction
import com.toyou.feature.home.viewmodel.HomeEvent
import com.toyou.feature.home.viewmodel.HomeViewModel
import com.toyou.feature.home.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToNotice: () -> Unit,
    onNavigateToHomeOption: () -> Unit,
    onNavigateToModify: () -> Unit,
    onNavigateToFriendCard: (Long) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeState by homeViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()

    LaunchedEffect(Unit) {
        userViewModel.getHomeEntry()
        homeViewModel.onAction(HomeAction.LoadYesterdayCards)
    }

    LaunchedEffect(Unit) {
        homeViewModel.event.collectLatest { event ->
            when (event) {
                is HomeEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is HomeEvent.TokenExpired -> {
                    // Handle token expiration
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent(
                isEmpty = homeState.isEmpty,
                onCardClick = { cardId -> onNavigateToFriendCard(cardId) }
            )
        },
        sheetPeekHeight = 120.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        modifier = modifier
    ) { paddingValues ->
        HomeContent(
            currentDate = homeState.currentDate,
            emotionText = getEmotionText(userState.emotion),
            emotion = userState.emotion,
            cardNum = userState.cardNum,
            hasUncheckedAlarm = userState.uncheckedAlarm,
            onNoticeClick = onNavigateToNotice,
            onEmotionClick = onNavigateToHomeOption,
            onMailboxClick = {
                if (userState.emotion != null) {
                    if (userState.cardId == null) {
                        onNavigateToCreate()
                    } else {
                        onNavigateToModify()
                    }
                } else {
                    Toast.makeText(context, "감정 우표를 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun HomeContent(
    currentDate: String,
    emotionText: String,
    emotion: String?,
    cardNum: Int,
    hasUncheckedAlarm: Boolean,
    onNoticeClick: () -> Unit,
    onEmotionClick: () -> Unit,
    onMailboxClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(getBackgroundColor(emotion))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with logo and notice icon
            TopBar(
                hasUncheckedAlarm = hasUncheckedAlarm,
                onNoticeClick = onNoticeClick
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Date
            Text(
                text = currentDate,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                modifier = Modifier
                    .background(
                        color = getDateBackgroundColor(emotion),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Emotion text
            Text(
                text = emotionText,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Emotion stamp
            Image(
                painter = painterResource(id = getEmotionImageRes(emotion)),
                contentDescription = "Emotion",
                modifier = Modifier
                    .size(60.dp, 70.dp)
                    .clickable { onEmotionClick() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mailbox
            Image(
                painter = painterResource(id = getMailboxImageRes(cardNum)),
                contentDescription = "Mailbox",
                modifier = Modifier.clickable { onMailboxClick() }
            )
        }
    }
}

@Composable
private fun TopBar(
    hasUncheckedAlarm: Boolean,
    onNoticeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.ToYou_hanguel),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        Box(
            modifier = Modifier.clickable { onNoticeClick() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_notice),
                contentDescription = "Notice"
            )
            if (hasUncheckedAlarm) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 8.dp)
                        .size(6.dp)
                        .background(Color.Red, shape = RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun BottomSheetContent(
    isEmpty: Boolean,
    onCardClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(50.dp)
                .height(4.dp)
                .background(Color.Gray, shape = RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = stringResource(R.string.home_bottom_sheet_title),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Content - Empty state for now
        if (isEmpty) {
            EmptyBottomSheetContent()
        } else {
            // TODO: Implement card list
            EmptyBottomSheetContent()
        }
    }
}

@Composable
private fun EmptyBottomSheetContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(77.dp))

        Image(
            painter = painterResource(id = R.drawable.home_bottomsheet_pseudo),
            contentDescription = "No cards"
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "아직 친구들이 일기카드를\n작성하지 않았어요",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center
        )
    }
}

private fun getEmotionText(emotion: String?): String {
    return when (emotion) {
        "HAPPY" -> "더없이 행복한 하루였어요"
        "EXCITED" -> "들뜨고 흥분돼요"
        "NORMAL" -> "평범한 하루였어요"
        "NERVOUS" -> "생각이 많아지고 불안해요"
        "ANGRY" -> "부글부글 화가 나요"
        else -> ""
    }
}

private fun getEmotionImageRes(emotion: String?): Int {
    return when (emotion) {
        "HAPPY" -> R.drawable.home_emotion_happy
        "EXCITED" -> R.drawable.home_emotion_exciting
        "NORMAL" -> R.drawable.home_emotion_normal
        "NERVOUS" -> R.drawable.home_emotion_anxiety
        "ANGRY" -> R.drawable.home_emotion_upset
        else -> R.drawable.home_emotion_none
    }
}

private fun getBackgroundColor(emotion: String?): Color {
    return when (emotion) {
        "HAPPY" -> Color(0xFFFFF9E6)
        "EXCITED" -> Color(0xFFE6F3FF)
        "NORMAL" -> Color(0xFFF3E6FF)
        "NERVOUS" -> Color(0xFFE6FFE6)
        "ANGRY" -> Color(0xFFFFE6E6)
        else -> Color.White
    }
}

private fun getDateBackgroundColor(emotion: String?): Color {
    return when (emotion) {
        "HAPPY" -> Color(0xFFFFE082)
        "EXCITED" -> Color(0xFF81D4FA)
        "NORMAL" -> Color(0xFFCE93D8)
        "NERVOUS" -> Color(0xFFA5D6A7)
        "ANGRY" -> Color(0xFFEF9A9A)
        else -> Color(0xFFE0E0E0)
    }
}

private fun getMailboxImageRes(cardNum: Int): Int {
    return when {
        cardNum == 0 -> R.drawable.home_mailbox_none
        cardNum in 1..5 -> R.drawable.home_mailbox_single
        else -> R.drawable.home_mailbox_multiple
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    ToYouTheme {
        HomeContent(
            currentDate = "2024년 1월 1일",
            emotionText = "더없이 행복한 하루였어요",
            emotion = "HAPPY",
            cardNum = 3,
            hasUncheckedAlarm = true,
            onNoticeClick = {},
            onEmotionClick = {},
            onMailboxClick = {}
        )
    }
}
