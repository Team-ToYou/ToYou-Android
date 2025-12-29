package com.toyou.feature.create.screen

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.toyou.core.designsystem.theme.ToYouTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.domain.model.PreviewCardModel
import com.toyou.feature.create.viewmodel.CardAction
import com.toyou.feature.create.viewmodel.CardEvent
import com.toyou.feature.create.viewmodel.CardViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PreviewScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is CardEvent.SendDataSuccess -> {
                    Toast.makeText(context, "일기카드가 저장되었습니다", Toast.LENGTH_SHORT).show()
                    viewModel.onAction(CardAction.ClearAllData)
                    onNavigateToHome()
                }
                is CardEvent.ShowError -> {
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
            title = "미리보기",
            onNavigationClick = { /* No back on preview */ }
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "일기카드 미리보기",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "저장 전 확인해주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { viewModel.onAction(CardAction.ToggleLock) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (uiState.exposure) "공개" else "비공개",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    Text(
                        text = if (uiState.exposure) "친구들이 이 일기카드를 볼 수 있습니다" else "나만 볼 수 있습니다",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = { viewModel.onAction(CardAction.ToggleLock) }) {
                    Icon(
                        imageVector = if (uiState.exposure) Icons.Outlined.Lock else Icons.Filled.Lock,
                        contentDescription = if (uiState.exposure) "Public" else "Private",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.previewCards) { card ->
                    PreviewCardItem(card = card)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            ToYouButton(
                text = "저장",
                onClick = {
                    viewModel.onAction(
                        CardAction.SendData(
                            previewCardModels = uiState.previewCards,
                            exposure = uiState.exposure
                        )
                    )
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun PreviewCardItem(
    card: PreviewCardModel,
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
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "from. ${card.fromWho}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(12.dp)
        ) {
            Text(
                text = card.answer.ifEmpty { "답변 없음" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (card.answer.isEmpty()) Color.Gray else Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScreenPreview() {
    ToYouTheme {
        PreviewScreen(
            onNavigateToHome = {}
        )
    }
}
