package com.example.cellnet.core.designsystem.appSnackbarHost

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cellnet.core.common.model.SnackbarInfoLevel
import com.example.cellnet.core.designsystem.theme.SnackbarErrorAction
import com.example.cellnet.core.designsystem.theme.SnackbarErrorContainer
import com.example.cellnet.core.designsystem.theme.SnackbarErrorContent
import com.example.cellnet.core.designsystem.theme.SnackbarInfoAction
import com.example.cellnet.core.designsystem.theme.SnackbarInfoContainer
import com.example.cellnet.core.designsystem.theme.SnackbarInfoContent
import com.example.cellnet.core.designsystem.theme.SnackbarSuccessAction
import com.example.cellnet.core.designsystem.theme.SnackbarSuccessContainer
import com.example.cellnet.core.designsystem.theme.SnackbarSuccessContent

@Composable
fun AppSnackBarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier,
    onDismiss: () -> Unit = { snackbarHostState.currentSnackbarData?.dismiss() },
    componentHeight: MutableState<Dp>,
    infoLevel: SnackbarInfoLevel
){
    var containerColor = MaterialTheme.colorScheme.inverseSurface
    var contentColor = MaterialTheme.colorScheme.inverseOnSurface
    var actionButtonColor = MaterialTheme.colorScheme.inversePrimary

    //Snackbar color changed according to the info level
    //Color directly referred from the color palette
    when (infoLevel) {
        SnackbarInfoLevel.SUCCESS -> {
            containerColor = SnackbarSuccessContainer
            contentColor = SnackbarSuccessContent
            actionButtonColor = SnackbarSuccessAction
        }

        SnackbarInfoLevel.ERROR -> {
            containerColor = SnackbarErrorContainer
            contentColor = SnackbarErrorContent
            actionButtonColor = SnackbarErrorAction
        }

        SnackbarInfoLevel.INFO -> {
            containerColor = SnackbarInfoContainer
            contentColor = SnackbarInfoContent
            actionButtonColor = SnackbarInfoAction
        }
    }

    SnackbarHost(
        modifier = modifier
            .wrapContentHeight(Alignment.Bottom),
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = modifier
                    .padding(bottom = componentHeight.value + 50.dp, start = 20.dp, end = 20.dp),
                shape = MaterialTheme.shapes.medium,
                containerColor = containerColor,
                action = {
                    data.visuals.actionLabel?.let {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Dismiss",
                                color = actionButtonColor,
                            )
                        }
                    }
                },
            ) {
                Text(
                    text = data.visuals.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    fontSize = 16.sp,
                )

            }
        },
    )
}