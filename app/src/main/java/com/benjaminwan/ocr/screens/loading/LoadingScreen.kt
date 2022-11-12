package com.benjaminwan.ocr.screens.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.benjaminwan.ocr.R
import com.benjaminwan.ocr.screens.Screen
import com.benjaminwan.ocr.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun LoadingScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(3.toDuration(DurationUnit.MILLISECONDS))
        navController.navigate(Screen.Main.route)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    AppTheme {
        LoadingScreen(rememberNavController())
    }
}