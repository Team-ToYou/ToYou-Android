package com.toyou.feature.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.feature.home.R
import kotlinx.coroutines.delay

@Composable
fun HomeResultScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-navigate after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToHome()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_emotion_happy),
            contentDescription = "Emotion stamp",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "감정우표가 저장되었어요!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "오늘 하루도 수고했어요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        ToYouButton(
            text = "홈으로",
            onClick = onNavigateToHome,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeResultScreenPreview() {
    ToYouTheme {
        HomeResultScreen(
            onNavigateToHome = {}
        )
    }
}
