package com.toyou.feature.create.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toyou.core.designsystem.theme.ToYouTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.domain.model.CardModel
import com.toyou.core.domain.model.CardShortModel
import com.toyou.core.domain.model.ChooseModel
import com.toyou.feature.create.viewmodel.CardAction
import com.toyou.feature.create.viewmodel.CardViewModel

@Composable
fun CreateScreen(
    onBackClick: () -> Unit,
    onNavigateToCreateWrite: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onAction(CardAction.GetAllData)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToYouTopAppBar(
            title = "일기카드 작성",
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
                text = "오늘의 질문을 선택해주세요",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "최대 10개까지 선택할 수 있습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selection count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${uiState.countSelection}/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.countSelection > 0) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.cards.isEmpty() && uiState.shortCards.isEmpty() && uiState.chooseCards.isEmpty()) {
                EmptyCardContent()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Short answer cards
                    if (uiState.shortCards.isNotEmpty()) {
                        item {
                            Text(
                                text = "짧은 답변",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                        itemsIndexed(uiState.shortCards) { index, card ->
                            ShortCardItem(
                                card = card,
                                isSelected = card.isButtonSelected,
                                onClick = {
                                    val newSelected = !card.isButtonSelected
                                    viewModel.onAction(CardAction.UpdateShortButtonState(index, newSelected))
                                    viewModel.onAction(CardAction.CountSelect(newSelected))
                                }
                            )
                        }
                    }

                    // Long answer cards
                    if (uiState.cards.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "긴 답변",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                        itemsIndexed(uiState.cards) { index, card ->
                            LongCardItem(
                                card = card,
                                isSelected = card.isButtonSelected,
                                onClick = {
                                    val newSelected = !card.isButtonSelected
                                    viewModel.onAction(CardAction.UpdateButtonState(index, newSelected))
                                    viewModel.onAction(CardAction.CountSelect(newSelected))
                                }
                            )
                        }
                    }

                    // Choose cards
                    if (uiState.chooseCards.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "선택형",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                        }
                        itemsIndexed(uiState.chooseCards) { index, card ->
                            ChooseCardItem(
                                card = card,
                                isSelected = card.isButtonSelected,
                                onClick = {
                                    val newSelected = !card.isButtonSelected
                                    viewModel.onAction(CardAction.UpdateChooseButton(index, newSelected))
                                    viewModel.onAction(CardAction.CountSelect(newSelected))
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            ToYouButton(
                text = "다음",
                onClick = {
                    viewModel.onAction(CardAction.UpdateAllPreviews)
                    onNavigateToCreateWrite()
                },
                enabled = uiState.countSelection > 0,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun ShortCardItem(
    card: CardShortModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = card.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "from. ${card.fromWho}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LongCardItem(
    card: CardModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = card.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "from. ${card.fromWho}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ChooseCardItem(
    card: ChooseModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = card.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "from. ${card.fromWho}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "선택지: ${card.options.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ColumnScope.EmptyCardContent(
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
            Text(
                text = "받은 질문이 없습니다",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "친구에게 질문을 요청해보세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateScreenPreview() {
    ToYouTheme {
        CreateScreen(
            onBackClick = {},
            onNavigateToCreateWrite = {}
        )
    }
}
