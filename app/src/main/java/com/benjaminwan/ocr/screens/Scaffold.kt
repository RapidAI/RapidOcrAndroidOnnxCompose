package com.benjaminwan.ocr.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.benjaminwan.ocr.ui.widget.snackbar.ColorSnackbar

@Composable
fun CommonScaffold(
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    scaffoldState: ScaffoldState = ScaffoldState(DrawerState(DrawerValue.Closed), SnackbarHostState()),
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            if (title != null)
                TopAppBar(
                    title = title,
                    modifier = Modifier
                        .fillMaxWidth(),
                    navigationIcon = navigationIcon,
                    actions = actions,
                )
        },
        bottomBar = bottomBar,
        snackbarHost = { hostState ->
            SnackbarHost(
                hostState = hostState,
                snackbar = { snackbarData ->
                    ColorSnackbar(
                        snackbarData = snackbarData,
                    )
                },
            )
        },
        content = content,
    )
}

