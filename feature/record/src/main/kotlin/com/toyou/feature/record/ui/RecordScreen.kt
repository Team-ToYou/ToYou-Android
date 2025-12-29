package com.toyou.feature.record.screen

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.record.R

@Composable
fun RecordScreen(
    onNavigateToMyCard: () -> Unit,
    onNavigateToFriendCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        RecordHeader()

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "기록을 확인해보세요",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "나와 친구들의 일기카드를 살펴보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecordCard(
                title = stringResource(R.string.calendar_my_record),
                subtitle = "나의 일기카드",
                imageRes = R.drawable.home_emotion_happy,
                backgroundColor = Color(0xFFFFF9E6),
                onClick = onNavigateToMyCard,
                modifier = Modifier.weight(1f)
            )

            RecordCard(
                title = stringResource(R.string.calendar_friend_record),
                subtitle = "친구 일기카드",
                imageRes = R.drawable.send_card,
                backgroundColor = Color(0xFFE6F3FF),
                onClick = onNavigateToFriendCard,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RecordHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.ToYou_hanguel),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
    }
}

@Composable
private fun RecordCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecordScreenPreview() {
    ToYouTheme {
        RecordScreen(
            onNavigateToMyCard = {},
            onNavigateToFriendCard = {}
        )
    }
}
