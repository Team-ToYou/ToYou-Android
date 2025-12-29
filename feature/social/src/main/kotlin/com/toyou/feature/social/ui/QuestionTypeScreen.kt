package com.toyou.feature.social.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.social.viewmodel.SocialAction
import com.toyou.feature.social.viewmodel.SocialViewModel

data class QuestionType(
    val type: String,
    val title: String,
    val description: String,
    val isLongAnswer: Boolean = false
)

private val questionTypes = listOf(
    QuestionType(
        type = "SHORT_ANSWER",
        title = "짧은 질문",
        description = "짧게 답할 수 있는 질문을 보내보세요"
    ),
    QuestionType(
        type = "LONG_ANSWER",
        title = "긴 질문",
        description = "자세한 답변이 필요한 질문을 보내보세요",
        isLongAnswer = true
    ),
    QuestionType(
        type = "OPTIONAL",
        title = "선택형 질문",
        description = "선택지가 있는 질문을 보내보세요"
    )
)

@Composable
fun QuestionTypeScreen(
    onBackClick: () -> Unit,
    onNavigateToContent: () -> Unit,
    onNavigateToContentLong: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToYouTopAppBar(
            title = "질문 유형 선택",
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Friend info section
            uiState.selectedEmotion?.let { emotion ->
                FriendInfoSection(
                    emotion = emotion,
                    ment = uiState.selectedEmotionMent
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "어떤 질문을 보낼까요?",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "질문 유형을 선택해주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question type options
            questionTypes.forEach { questionType ->
                QuestionTypeItem(
                    questionType = questionType,
                    onClick = {
                        viewModel.onAction(SocialAction.SetTypeFriend(questionType.type))
                        if (questionType.isLongAnswer) {
                            onNavigateToContentLong()
                        } else {
                            onNavigateToContent()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun FriendInfoSection(
    emotion: Int,
    ment: String,
    modifier: Modifier = Modifier
) {
    val emotionText = when (emotion) {
        1 -> "행복"
        2 -> "신남"
        3 -> "보통"
        4 -> "불안"
        5 -> "화남"
        else -> ""
    }

    val backgroundColor = when (emotion) {
        1 -> Color(0xFFFFF9E6)
        2 -> Color(0xFFE6F3FF)
        3 -> Color(0xFFF3E6FF)
        4 -> Color(0xFFE6FFE6)
        5 -> Color(0xFFFFE6E6)
        else -> Color(0xFFF5F5F5)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "친구의 오늘 기분: $emotionText",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            if (ment.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$ment\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun QuestionTypeItem(
    questionType: QuestionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = questionType.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = questionType.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionTypeScreenPreview() {
    ToYouTheme {
        QuestionTypeItem(
            questionType = QuestionType(
                type = "SHORT_ANSWER",
                title = "짧은 질문",
                description = "짧게 답할 수 있는 질문을 보내보세요"
            ),
            onClick = {}
        )
    }
}
