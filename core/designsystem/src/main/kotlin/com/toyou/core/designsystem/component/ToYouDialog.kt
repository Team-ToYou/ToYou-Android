package com.toyou.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.toyou.core.designsystem.theme.ToYouTheme

@Composable
fun ToYouDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    confirmText: String = "확인",
    dismissText: String = "취소",
    confirmTextColor: Color = MaterialTheme.colorScheme.primary,
    dismissTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onConfirm: () -> Unit = {},
    showDismissButton: Boolean = true
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (showDismissButton) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = dismissText,
                                color = dismissTextColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = confirmText,
                            color = confirmTextColor
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ToYouDialogPreview() {
    ToYouTheme {
        ToYouDialog(
            title = "정말 로그아웃하시겠어요?",
            onDismiss = {},
            onConfirm = {},
            dismissText = "취소",
            confirmText = "로그아웃",
            confirmTextColor = Color.Red
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToYouDialogWithSubtitlePreview() {
    ToYouTheme {
        ToYouDialog(
            title = "정말 탈퇴하시겠어요?",
            subtitle = "작성하신 일기카드가 모두\n삭제되며 복구할 수 없어요",
            onDismiss = {},
            onConfirm = {},
            dismissText = "탈퇴하기",
            confirmText = "취소"
        )
    }
}
