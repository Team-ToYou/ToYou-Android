package com.toyou.feature.record.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.record.R
import com.toyou.core.domain.model.DiaryCard
import com.toyou.feature.record.viewmodel.MyRecordAction
import com.toyou.feature.record.viewmodel.MyRecordViewModel
import java.util.Calendar

@Composable
fun MyCardContainerScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        viewModel.onAction(MyRecordAction.LoadDiaryCards(year, month))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToYouTopAppBar(
            title = "나의 일기카드",
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "나의 일기카드",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "내가 작성한 일기카드들을 확인해보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.diaryCards.isEmpty()) {
                EmptyMyRecordsContent()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.diaryCards) { record ->
                        MyRecordCardItem(
                            record = record,
                            onClick = { /* Navigate to card detail */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyRecordCardItem(
    record: DiaryCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = getEmotionBackgroundColor(record.emotion)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = record.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getEmotionText(record.emotion),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )

                Image(
                    painter = painterResource(id = getEmotionDrawable(record.emotion)),
                    contentDescription = "Emotion",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.EmptyMyRecordsContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_emotion_none),
                contentDescription = "No records",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "아직 작성한 일기카드가 없어요",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getEmotionBackgroundColor(emotion: String?): Color {
    return when (emotion?.uppercase()) {
        "HAPPY" -> Color(0xFFFFF9E6)
        "EXCITED" -> Color(0xFFE6F3FF)
        "NORMAL" -> Color(0xFFF3E6FF)
        "NERVOUS" -> Color(0xFFE6FFE6)
        "ANGRY" -> Color(0xFFFFE6E6)
        else -> Color(0xFFF5F5F5)
    }
}

private fun getEmotionText(emotion: String?): String {
    return when (emotion?.uppercase()) {
        "HAPPY" -> "행복"
        "EXCITED" -> "신남"
        "NORMAL" -> "보통"
        "NERVOUS" -> "불안"
        "ANGRY" -> "화남"
        else -> ""
    }
}

private fun getEmotionDrawable(emotion: String?): Int {
    return when (emotion?.uppercase()) {
        "HAPPY" -> R.drawable.home_emotion_happy
        "EXCITED" -> R.drawable.home_emotion_exciting
        "NORMAL" -> R.drawable.home_emotion_normal
        "NERVOUS" -> R.drawable.home_emotion_anxiety
        "ANGRY" -> R.drawable.home_emotion_upset
        else -> R.drawable.home_emotion_none
    }
}

@Preview(showBackground = true)
@Composable
private fun MyCardContainerScreenPreview() {
    ToYouTheme {
        MyCardContainerScreen(
            onBackClick = {}
        )
    }
}
