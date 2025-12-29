package com.toyou.feature.social.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.social.R
import com.toyou.feature.social.viewmodel.SocialAction
import com.toyou.feature.social.viewmodel.SocialViewModel
import kotlinx.coroutines.delay

@Composable
fun SendFinalScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SocialViewModel = hiltViewModel()
) {
    // Reset question data when leaving
    LaunchedEffect(Unit) {
        viewModel.onAction(SocialAction.ResetQuestionData)
    }

    // Auto-navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
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
            painter = painterResource(id = R.drawable.send_card),
            contentDescription = "Question sent",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "질문을 보냈어요!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "친구가 답변을 작성하면 알림을 보내드릴게요",
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
private fun SendFinalScreenPreview() {
    ToYouTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "질문을 보냈어요!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "친구가 답변을 작성하면 알림을 보내드릴게요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
