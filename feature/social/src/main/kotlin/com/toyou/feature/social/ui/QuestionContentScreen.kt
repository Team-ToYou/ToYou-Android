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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
fun QuestionContentScreen(
    onBackClick: () -> Unit,
    onNavigateToSend: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var questionContent by remember { mutableStateOf("") }
    var isAnonymous by remember { mutableStateOf(false) }
    val options = remember { mutableStateListOf<String>() }
    var newOption by remember { mutableStateOf("") }

    val isOptional = uiState.questionForm.type == "OPTIONAL"

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
                text = "질문을 작성해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                BasicTextField(
                    value = questionContent,
                    onValueChange = { if (it.length <= 100) questionContent = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (questionContent.isEmpty()) {
                                Text(
                                    text = "친구에게 보낼 질문을 입력하세요",
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
                    text = "${questionContent.length}/100",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Options section for OPTIONAL type
            if (isOptional) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "선택지 추가",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Existing options
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                        IconButton(onClick = { options.removeAt(index) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove option",
                                tint = Color.Gray
                            )
                        }
                    }
                }

                // Add new option
                if (options.size < 4) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            BasicTextField(
                                value = newOption,
                                onValueChange = { newOption = it },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Black
                                ),
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (newOption.isEmpty()) {
                                            Text(
                                                text = "선택지 입력",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                        IconButton(
                            onClick = {
                                if (newOption.isNotBlank()) {
                                    options.add(newOption)
                                    newOption = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add option",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
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

            val isButtonEnabled = questionContent.isNotBlank() &&
                    (!isOptional || options.size >= 2)

            ToYouButton(
                text = "다음",
                onClick = {
                    viewModel.onAction(SocialAction.SetAnonymous(isAnonymous))
                    if (isOptional) {
                        viewModel.onAction(SocialAction.UpdateQuestionOptions(options.toList()))
                    }
                    onNavigateToSend()
                },
                enabled = isButtonEnabled,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionContentScreenPreview() {
    ToYouTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text(
                text = "질문을 작성해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        }
    }
}
