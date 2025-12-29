package com.toyou.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toyou.core.designsystem.theme.ToYouTheme

@Composable
fun ToYouTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    maxLength: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            maxLines = maxLines,
            label = label?.let { { Text(it) } },
            placeholder = { Text(placeholder) },
            isError = isError,
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() },
                onNext = { onImeAction() },
                onSearch = { onImeAction() }
            )
        )

        if (maxLength != null) {
            Text(
                text = "${value.length}/$maxLength",
                style = MaterialTheme.typography.labelSmall,
                color = if (value.length >= maxLength) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ToYouTextFieldPreview() {
    ToYouTheme {
        ToYouTextField(
            value = "",
            onValueChange = {},
            placeholder = "닉네임을 입력하세요",
            label = "닉네임",
            maxLength = 15
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToYouTextFieldFilledPreview() {
    ToYouTheme {
        ToYouTextField(
            value = "투유",
            onValueChange = {},
            label = "닉네임",
            maxLength = 15
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToYouTextFieldErrorPreview() {
    ToYouTheme {
        ToYouTextField(
            value = "투유",
            onValueChange = {},
            label = "닉네임",
            isError = true,
            supportingText = "이미 사용 중인 닉네임입니다"
        )
    }
}
