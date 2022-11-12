package com.benjaminwan.ocr.screens.imei

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.benjaminwan.ocr.screens.CommonScaffold
import com.benjaminwan.ocr.screens.NavBackIcon
import com.benjaminwan.ocr.screens.Screen

@Composable
fun ImeiScreen(navController: NavHostController) {
    CommonScaffold(
        modifier = Modifier.fillMaxSize(),
        title = { Text(text = stringResource(id = Screen.Imei.titleId)) },
        navigationIcon = { NavBackIcon(navController) },
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Text(text = "开发中，待后续版本更新……")
        }
    }
}