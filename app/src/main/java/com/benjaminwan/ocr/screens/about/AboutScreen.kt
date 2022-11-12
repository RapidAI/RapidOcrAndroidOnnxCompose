package com.benjaminwan.ocr.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.benjaminwan.ocr.BuildConfig
import com.benjaminwan.ocr.R
import com.benjaminwan.ocr.screens.CommonScaffold
import com.benjaminwan.ocr.screens.NavBackIcon
import com.benjaminwan.ocr.screens.Screen
import com.benjaminwan.ocr.ui.theme.AppTheme
import com.benjaminwan.ocr.ui.widget.RowInfoView
import com.benjaminwan.ocr.utils.getAppVersionName
import com.benjaminwan.ocr.utils.toClipboard

@Composable
fun AboutScreen(navController: NavHostController) {
    val context = LocalContext.current
    val append = if (BuildConfig.DEBUG) "-debug" else ""
    val versionName = "${getAppVersionName(context)}$append"
    val appUrl = "https://github.com/RapidAI/RapidOcrAndroidOnnxCompose"
    CommonScaffold(
        modifier = Modifier.fillMaxSize(),
        title = { Text(text = stringResource(id = Screen.About.titleId)) },
        navigationIcon = { NavBackIcon(navController) },
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            AppLogo(Modifier.fillMaxWidth())
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
            Text(text = versionName, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                item {
                    RowInfoView(header = "作者", content = "https://github.com/benjaminwan")
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    RowInfoView(header = "源码", content = appUrl)
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    context.toClipboard(appUrl)
                }
            ) {
                Text(text = "仓库地址复制到剪切板")
            }
        }
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.onPrimary
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AppTheme {
        AboutScreen(rememberNavController())
    }
}