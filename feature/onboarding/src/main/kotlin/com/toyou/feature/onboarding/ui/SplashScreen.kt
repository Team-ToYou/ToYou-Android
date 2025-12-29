package com.toyou.feature.onboarding.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.toyou.core.datastore.TokenStorage
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.onboarding.R
import com.toyou.feature.onboarding.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

private val GangwonFontFamily = FontFamily(
    Font(R.font.gangwoneduhyeonokt)
)

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        delay(1500)
        viewModel.checkIfTokenExists()
    }

    LaunchedEffect(uiState.checkIfTokenExists, uiState.isInitialization) {
        if (!uiState.isInitialization && uiState.checkIfTokenExists) {
            onNavigateToHome()
        } else if (!uiState.isInitialization && !uiState.checkIfTokenExists && uiState.loginSuccess == false) {
            onNavigateToLogin()
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_logo),
                contentDescription = "ToYou Logo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.ToYou_hanguel),
                fontFamily = GangwonFontFamily,
                fontSize = 46.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    ToYouTheme {
        SplashContent()
    }
}
