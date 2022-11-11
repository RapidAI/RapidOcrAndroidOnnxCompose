package com.benjaminwan.ocr.ui.widget.snackbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.benjaminwan.ocr.R

@Composable
fun ColorSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    elevation: Dp = 4.dp
) {
    val toastType = when (snackbarData.actionLabel) {
        SnackType.Success.name -> SnackType.Success
        SnackType.Info.name -> SnackType.Info
        SnackType.Warning.name -> SnackType.Warning
        SnackType.Error.name -> SnackType.Error
        else -> null
    }
    if (toastType != null) {
        Snackbar(
            modifier = modifier,
            content = { Text(snackbarData.message) },
            action = {
                Row(
                    modifier = Modifier.clickable(true, onClick = { snackbarData.performAction() }),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (toastType.icon > 0) Icon(painter = painterResource(toastType.icon), contentDescription = "")
                    Text(stringResource(id = R.string.common_close))
                }
            },
            actionOnNewLine = actionOnNewLine,
            shape = shape,
            backgroundColor = toastType.color,
            contentColor = Color(0xFFF7F7F7),
            elevation = elevation
        )
    }
}