package com.example.cellnet.core.designsystem.outlinedTextFieldWithErrorLabel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OutlinedTextFieldWithErrorLabel(
    modifier: Modifier = Modifier,
    value: String,
    labelText: String,
    onValueChange: (String) -> Unit,
    focusManager: FocusManager,
    errorMsg: String,
    keyboardType: KeyboardType,
    validateOnFocusChange: () -> Unit,
    isTextVisible: Boolean,
) {
    Column {
        OutlinedTextField(
            modifier = modifier
                .padding(top = 15.dp)
                .onFocusChanged {
                    validateOnFocusChange()
                },
            singleLine = true,
            value = value,
            label = { Text(text = labelText) },
            onValueChange = { onValueChange(it) },
            isError = errorMsg != "",
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = keyboardType
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.moveFocus(FocusDirection.Next)
                }
            ),
            visualTransformation = if (isTextVisible) VisualTransformation.None
                else PasswordVisualTransformation(),

            )

        if (errorMsg != "") {
            Text(
                modifier = modifier
                    .padding(start = 10.dp),
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Left,
                fontSize = 14.sp
            )
        }
    }
}