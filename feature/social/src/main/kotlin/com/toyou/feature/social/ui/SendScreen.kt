package com.toyou.feature.social.screen

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.feature.social.viewmodel.SocialAction
import com.toyou.feature.social.viewmodel.SocialEvent
import com.toyou.feature.social.viewmodel.SocialViewModel
import com.toyou.feature.home.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SendScreen(
    onBackClick: () -> Unit,
    onNavigateToSendFinal: () -> Unit,
    modifier: Modifier = Modifier,
    socialViewModel: SocialViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val socialState by socialViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        socialViewModel.event.collectLatest { event ->
            when (event) {
                is SocialEvent.QuestionSent -> {
                    onNavigateToSendFinal()
                }
                is SocialEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
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
            title = "질문 보내기",
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "질문을 확인해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "친구에게 보낼 질문을 미리보기할 수 있어요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preview card
            QuestionPreviewCard(
                questionType = socialState.questionForm.type,
                questionContent = socialState.questionForm.content,
                options = socialState.questionForm.options,
                isAnonymous = socialState.questionForm.anonymous
            )

            Spacer(modifier = Modifier.weight(1f))

            ToYouButton(
                text = "보내기",
                onClick = {
                    socialViewModel.onAction(
                        SocialAction.SendQuestion(userState.nickname ?: "")
                    )
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun QuestionPreviewCard(
    questionType: String,
    questionContent: String,
    options: List<String>?,
    isAnonymous: Boolean,
    modifier: Modifier = Modifier
) {
    val typeLabel = when (questionType) {
        "SHORT_ANSWER" -> "짧은 질문"
        "LONG_ANSWER" -> "긴 질문"
        "OPTIONAL" -> "선택형 질문"
        else -> "질문"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = questionContent.ifEmpty { "질문 내용이 여기에 표시됩니다" },
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            if (!options.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                options.forEachIndexed { index, option ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "${index + 1}. $option",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isAnonymous) "익명으로 전송됩니다" else "닉네임으로 전송됩니다",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SendScreenPreview() {
    ToYouTheme {
        QuestionPreviewCard(
            questionType = "SHORT_ANSWER",
            questionContent = "오늘 하루 어떠셨나요?",
            options = null,
            isAnonymous = false
        )
    }
}
