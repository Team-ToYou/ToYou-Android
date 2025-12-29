package com.toyou.feature.onboarding.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.onboarding.R
import kotlinx.coroutines.delay

data class TutorialStep(
    val titleRes: Int,
    val subtitleRes: Int? = null,
    val imageRes: Int
)

private val tutorialSteps = listOf(
    TutorialStep(
        titleRes = R.string.tutorial_step_1_title,
        subtitleRes = R.string.tutorial_step_1_subtitle,
        imageRes = R.drawable.tutorial_step_1_3x
    ),
    TutorialStep(
        titleRes = R.string.tutorial_step_2_title,
        subtitleRes = R.string.tutorial_step_2_subtitle,
        imageRes = R.drawable.tutorial_step_2
    ),
    TutorialStep(
        titleRes = R.string.tutorial_step_3_title,
        subtitleRes = R.string.tutorial_step_3_subtitle,
        imageRes = R.drawable.tutorial_step_3
    ),
    TutorialStep(
        titleRes = R.string.tutorial_step_4_title,
        subtitleRes = R.string.tutorial_step_4_subtitle,
        imageRes = R.drawable.tutorial_step_4_may
    ),
    TutorialStep(
        titleRes = R.string.tutorial_step_5_title,
        subtitleRes = null,
        imageRes = R.drawable.tutorial_step_5_3x
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { tutorialSteps.size })
    val isLastPage = pagerState.currentPage == tutorialSteps.size - 1
    var showCompleteButton by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == tutorialSteps.size - 1) {
            delay(500)
            showCompleteButton = true
        } else {
            showCompleteButton = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE6E6E6))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Skip button at top right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                if (!isLastPage) {
                    Text(
                        text = stringResource(R.string.tutorial_skip),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.clickable { onComplete() }
                    )
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                TutorialPageContent(
                    step = tutorialSteps[page],
                    isLastPage = page == tutorialSteps.size - 1,
                    showCompleteButton = showCompleteButton && page == tutorialSteps.size - 1,
                    onComplete = onComplete
                )
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(tutorialSteps.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == pagerState.currentPage) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.LightGray
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun TutorialPageContent(
    step: TutorialStep,
    isLastPage: Boolean,
    showCompleteButton: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(step.titleRes),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        step.subtitleRes?.let { subtitleRes ->
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(step.imageRes),
                contentDescription = "Tutorial image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        if (isLastPage) {
            AnimatedVisibility(
                visible = showCompleteButton,
                enter = fadeIn()
            ) {
                Text(
                    text = stringResource(R.string.tutorial_complete),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .clickable { onComplete() }
                )
            }
            if (!showCompleteButton) {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TutorialScreenPreview() {
    ToYouTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6E6E6))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = "건너뛰기",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "투유는 맞춤형 질문과 친구들이 보낸 질문으로\n오늘의 일기카드를 만드는 서비스예요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "하루하루 그날의 감정을 기록할 수 있어요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (index == 0) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 0)
                                        Color(0xFFEA9797)
                                    else
                                        Color.LightGray
                                )
                        )
                    }
                }
            }
        }
    }
}
