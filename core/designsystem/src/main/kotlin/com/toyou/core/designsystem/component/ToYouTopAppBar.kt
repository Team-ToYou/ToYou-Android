package com.toyou.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.toyou.core.designsystem.theme.ToYouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToYouTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로 가기"
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ToYouTopAppBarPreview() {
    ToYouTheme {
        ToYouTopAppBar(
            title = "닉네임 설정",
            onNavigationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToYouTopAppBarNoNavPreview() {
    ToYouTheme {
        ToYouTopAppBar(
            title = "홈"
        )
    }
}
