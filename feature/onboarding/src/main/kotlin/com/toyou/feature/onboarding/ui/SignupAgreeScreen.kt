package com.toyou.feature.onboarding.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toyou.core.designsystem.component.ToYouButton
import com.toyou.core.designsystem.component.ToYouTopAppBar
import com.toyou.core.designsystem.theme.ToYouTheme
import com.toyou.feature.onboarding.R

@Composable
fun SignupAgreeScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTermsClick: (() -> Unit)? = null
) {
    val checkStates = remember { mutableStateListOf(false, false, false, false) }
    val allChecked = checkStates.all { it }
    val requiredChecked = checkStates[1] && checkStates[2] && checkStates[3] // 필수 항목들

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToYouTopAppBar(
            title = stringResource(R.string.signup_screen_bar_title),
            onNavigationClick = onBackClick
        )

        HorizontalDivider(color = Color(0xFFF5F5F5))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.signup_agree_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.signup_agree_sub_title),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 전체 선택
            AgreementItem(
                text = stringResource(R.string.signup_agree_select_1),
                isChecked = allChecked,
                onCheckedChange = { checked ->
                    for (i in checkStates.indices) {
                        checkStates[i] = checked
                    }
                },
                isSelectAll = true
            )

            HorizontalDivider(
                color = Color(0xFFF5F5F5),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // 만 14세 이상
            AgreementItem(
                text = stringResource(R.string.signup_agree_select_2),
                isChecked = checkStates[1],
                onCheckedChange = { checkStates[1] = it },
                isRequired = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 이용약관 동의
            AgreementItem(
                text = stringResource(R.string.signup_agree_select_3),
                isChecked = checkStates[2],
                onCheckedChange = { checkStates[2] = it },
                isRequired = true,
                showArrow = true,
                onArrowClick = onTermsClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 개인정보 수집 및 이용동의
            AgreementItem(
                text = stringResource(R.string.signup_agree_select_4),
                isChecked = checkStates[3],
                onCheckedChange = { checkStates[3] = it },
                isRequired = true,
                showArrow = true,
                onArrowClick = onTermsClick
            )

            Spacer(modifier = Modifier.weight(1f))

            ToYouButton(
                text = stringResource(R.string.next_button),
                onClick = onNextClick,
                enabled = requiredChecked,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun AgreementItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isSelectAll: Boolean = false,
    isRequired: Boolean = false,
    showArrow: Boolean = false,
    onArrowClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = if (isChecked) R.drawable.checkbox_checked else R.drawable.checkbox_uncheck
            ),
            contentDescription = if (isChecked) "Checked" else "Unchecked",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = if (isSelectAll) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (isRequired) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = stringResource(R.string.signup_agree_essential),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View details",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onArrowClick?.invoke() },
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignupAgreeScreenPreview() {
    ToYouTheme {
        SignupAgreeScreen(
            onBackClick = {},
            onNextClick = {},
            onTermsClick = {}
        )
    }
}
