package com.toyou.feature.create.screen

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.feature.create.viewmodel.CardAction
import com.toyou.feature.create.viewmodel.CardEvent
import com.toyou.feature.create.viewmodel.CardViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateWriteScreen(
    onBackClick: () -> Unit,
    onNavigateToPreview: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val answers = remember { mutableStateMapOf<Int, String>() }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CardEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    CreateWriteContent(
        previewCards = uiState.previewCards,
        isAllAnswersFilled = uiState.isAllAnswersFilled,
        answers = answers,
        onBackClick = onBackClick,
        onNavigateToPreview = onNavigateToPreview,
        onAnswerChange = { cardId, index, type, answer ->
            answers[cardId] = answer
            val isFilled = answer.isNotBlank()
            when (type) {
                0 -> viewModel.onAction(CardAction.UpdateCardInputStatus(index, isFilled))
                1 -> viewModel.onAction(CardAction.UpdateCardInputStatusLong(index, isFilled))
                else -> viewModel.onAction(CardAction.UpdateCardInputStatusChoose(index, isFilled))
            }
        },
        onClearFocus = { focusManager.clearFocus() },
        modifier = modifier
    )
}

@Composable
private fun CreateWriteContent(
    previewCards: List<PreviewCardModel>,
    isAllAnswersFilled: Boolean,
    answers: Map<Int, String>,
    onBackClick: () -> Unit,
    onNavigateToPreview: () -> Unit,
    onAnswerChange: (cardId: Int, index: Int, type: Int, answer: String) -> Unit,
    onClearFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClearFocus() }
    ) {
        ToYouTopAppBar(
            title = "답변 작성",
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
                text = "질문에 답변해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "선택한 질문에 대한 답변을 작성해주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(previewCards) { index, card ->
                    AnswerCardItem(
                        card = card,
                        answer = answers[card.id.toInt()] ?: "",
                        onAnswerChange = { newAnswer ->
                            onAnswerChange(card.id.toInt(), index, card.type, newAnswer)
                        },
                        selectedOption = answers[card.id.toInt()]
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            ToYouButton(
                text = "다음",
                onClick = onNavigateToPreview,
                enabled = isAllAnswersFilled,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun AnswerCardItem(
    card: PreviewCardModel,
    answer: String,
    onAnswerChange: (String) -> Unit,
    selectedOption: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = card.question,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "from. ${card.fromWho}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        val options = card.options
        if (!options.isNullOrEmpty()) {
            // Choose type - show options
            options.forEach { option ->
                val isSelected = selectedOption == option
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.White)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onAnswerChange(option) }
                        .padding(12.dp)
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                    )
                }
            }
        } else {
            // Text answer
            val maxLength = if (card.type == 1) 200 else 50
            val height = if (card.type == 1) 120.dp else 80.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = answer,
                    onValueChange = { if (it.length <= maxLength) onAnswerChange(it) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (answer.isEmpty()) {
                                Text(
                                    text = "답변을 입력해주세요",
                                    style = MaterialTheme.typography.bodyMedium,
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
                    text = "${answer.length}/$maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CreateWriteScreenPreview() {
    val sampleCards = listOf(
        PreviewCardModel(
            id = 1,
            question = "오늘 가장 기억에 남는 일은 무엇인가요?",
            fromWho = "익명",
            answer = "",
            type = 0,
            options = null
        ),
        PreviewCardModel(
            id = 2,
            question = "요즘 어떤 음악을 자주 듣나요?",
            fromWho = "친구1",
            answer = "",
            type = 1,
            options = null
        ),
        PreviewCardModel(
            id = 3,
            question = "오늘 저녁 뭐 먹을래?",
            fromWho = "친구2",
            answer = "",
            type = 2,
            options = listOf("치킨", "피자", "햄버거", "한식")
        )
    )

    ToYouTheme {
        CreateWriteContent(
            previewCards = sampleCards,
            isAllAnswersFilled = false,
            answers = emptyMap(),
            onBackClick = {},
            onNavigateToPreview = {},
            onAnswerChange = { _, _, _, _ -> },
            onClearFocus = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnswerCardItemPreview() {
    ToYouTheme {
        AnswerCardItem(
            card = PreviewCardModel(
                id = 1,
                question = "오늘 가장 기억에 남는 일은 무엇인가요?",
                fromWho = "익명",
                answer = "",
                type = 0,
                options = null
            ),
            answer = "오늘 친구와 맛있는 저녁을 먹었어요!",
            onAnswerChange = {},
            selectedOption = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnswerCardItemOptionsPreview() {
    ToYouTheme {
        AnswerCardItem(
            card = PreviewCardModel(
                id = 1,
                question = "오늘 저녁 뭐 먹을래?",
                fromWho = "친구",
                answer = "",
                type = 2,
                options = listOf("치킨", "피자", "햄버거")
            ),
            answer = "",
            onAnswerChange = {},
            selectedOption = "치킨"
        )
    }
}
