package com.benjaminwan.ocr.screens.loading

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.benjaminwan.ocr.R
import com.benjaminwan.ocr.screens.Screen
import com.benjaminwan.ocr.ui.theme.AppTheme
import com.google.accompanist.permissions.*
import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private fun gotoSettings(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoadingScreen(navController: NavHostController) {
    val context = LocalContext.current
    var requesting by remember { mutableStateOf(false) }
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        ),
        onPermissionsResult = { results ->
            requesting = false
        },
    )
    val onRequest: () -> Unit = {
        multiplePermissionsState.launchMultiplePermissionRequest()
        requesting = true
    }

    val onGotoSetting: () -> Unit = {
        gotoSettings(context)
    }

    var delayTime by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        onRequest()
        while (!multiplePermissionsState.allPermissionsGranted) {
            delay(10)
        }
        (3 downTo 1).forEach {
            delayTime = it
            delay(1.toDuration(DurationUnit.SECONDS))
        }
        navController.navigate(Screen.Main.route)
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        val minSide = min(this.maxHeight, this.maxWidth)
        if (requesting) {
            CircularProgressIndicator(modifier = Modifier.size(minSide), color = MaterialTheme.colors.onPrimary)
        } else {
            RequestView(multiplePermissionsState, onRequest, onGotoSetting, delayTime)
        }
        Image(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.FillWidth
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestView(
    multiplePermissionsState: MultiplePermissionsState,
    onRequest: () -> Unit,
    onGotoSetting: () -> Unit,
    delayTime: Int,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (multiplePermissionsState.allPermissionsGranted) {
            Text(
                text = "权限申请通过，启动中……$delayTime",
                color = MaterialTheme.colors.onPrimary,
            )
        } else if (multiplePermissionsState.shouldShowRationale) {
            Text(
                text = "权限申请未全部通过，App无法正常运行，请重新申请权限",
                color = MaterialTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
            ) {
                Text("申请权限", color = MaterialTheme.colors.onPrimary)
            }
        } else {
            Text(
                text = "权限申请未全部通过，App无法正常运行，请手动授予权限",
                color = MaterialTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onGotoSetting,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
            ) {
                Text("去手工授权", color = MaterialTheme.colors.onPrimary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    AppTheme {
        LoadingScreen(rememberNavController())
    }
}