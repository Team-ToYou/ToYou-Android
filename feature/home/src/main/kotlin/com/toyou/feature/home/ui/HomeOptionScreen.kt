package com.toyou.feature.home.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toyou.core.designsystem.theme.ToYouTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.feature.home.R
import com.toyou.core.network.model.emotion.EmotionRequest
import com.toyou.feature.home.viewmodel.HomeOptionAction
import com.toyou.feature.home.viewmodel.HomeOptionViewModel

data class EmotionOption(
    val type: String,
    val titleRes: Int,
    val imageRes: Int,
    val backgroundColor: Color,
    val borderColor: Color
)

private val emotionOptions = listOf(
    EmotionOption(
        type = "HAPPY",
        titleRes = R.string.home_stamp_option_happy,
        imageRes = R.drawable.home_emotion_happy,
        backgroundColor = Color(0xFFFFF9E6),
        borderColor = Color(0xFFFFE082)
    ),
    EmotionOption(
        type = "EXCITED",
        titleRes = R.string.home_stamp_option_exciting,
        imageRes = R.drawable.home_emotion_exciting,
        backgroundColor = Color(0xFFE6F3FF),
        borderColor = Color(0xFF81D4FA)
    ),
    EmotionOption(
        type = "NORMAL",
        titleRes = R.string.home_stamp_option_normal,
        imageRes = R.drawable.home_emotion_normal,
        backgroundColor = Color(0xFFF3E6FF),
        borderColor = Color(0xFFCE93D8)
    ),
    EmotionOption(
        type = "NERVOUS",
        titleRes = R.string.home_stamp_option_anxiety,
        imageRes = R.drawable.home_emotion_anxiety,
        backgroundColor = Color(0xFFE6FFE6),
        borderColor = Color(0xFFA5D6A7)
    ),
    EmotionOption(
        type = "ANGRY",
        titleRes = R.string.home_stamp_option_upset,
        imageRes = R.drawable.home_emotion_upset,
        backgroundColor = Color(0xFFFFE6E6),
        borderColor = Color(0xFFEF9A9A)
    )
)

@Composable
fun HomeOptionScreen(
    onBackClick: () -> Unit,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeOptionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    var selectedEmotion by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = stringResource(R.string.home_stamp_title_1),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Text(
                text = stringResource(R.string.home_stamp_title_2),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.home_stamp_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Emotion options
            emotionOptions.forEach { emotion ->
                EmotionOptionItem(
                    emotion = emotion,
                    isSelected = selectedEmotion == emotion.type,
                    onClick = { selectedEmotion = emotion.type }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm button
            ToYouButton(
                text = stringResource(R.string.complete_button),
                onClick = {
                    selectedEmotion?.let { emotionType ->
                        viewModel.onAction(
                            HomeOptionAction.UpdateEmotion(
                                EmotionRequest(emotion = emotionType)
                            )
                        )
                        onNavigateToResult()
                    } ?: run {
                        Toast.makeText(context, "감정을 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = selectedEmotion != null,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun EmotionOptionItem(
    emotion: EmotionOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) emotion.backgroundColor else Color(0xFFF5F5F5))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) emotion.borderColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = emotion.imageRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(emotion.titleRes),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeOptionScreenPreview() {
    ToYouTheme {
        HomeOptionScreen(
            onBackClick = {},
            onNavigateToResult = {}
        )
    }
}
