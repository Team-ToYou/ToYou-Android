package com.toyou.feature.social.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.feature.social.viewmodel.SocialAction
import com.toyou.feature.social.viewmodel.SocialViewModel

@Composable
fun QuestionContentLongScreen(
    onBackClick: () -> Unit,
    onNavigateToSend: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var questionContent by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }

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
            title = "질문 작성",
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
                text = "자세한 질문을 작성해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "긴 답변이 필요한 질문을 보내보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question input - larger for long questions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = questionContent,
                    onValueChange = { if (it.length <= 200) questionContent = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (questionContent.isEmpty()) {
                                Text(
                                    text = "자세한 답변이 필요한 질문을 입력하세요",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${questionContent.length}/200",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Anonymous checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isAnonymous = !isAnonymous },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAnonymous,
                    onCheckedChange = { isAnonymous = it }
                )
                Text(
                    text = "익명으로 보내기",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ToYouButton(
                text = "다음",
                onClick = {
                    viewModel.onAction(SocialAction.SetAnonymous(isAnonymous))
                    onNavigateToSend()
                },
                enabled = questionContent.isNotBlank(),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionContentLongScreenPreview() {
    ToYouTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(
                text = "자세한 질문을 작성해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "긴 답변이 필요한 질문을 보내보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
