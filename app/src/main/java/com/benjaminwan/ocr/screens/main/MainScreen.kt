package com.benjaminwan.ocr.screens.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.benjaminwan.ocr.screens.CommonScaffold
import com.benjaminwan.ocr.screens.Screen.Companion.menuList

@Composable
fun MainScreen(navController: NavHostController) {
    CommonScaffold(
        modifier = Modifier.fillMaxSize(),
        //title = { Text(text = Screen.Main.title) },
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h6) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    columns = GridCells.Fixed(2),
                ) {
                    menuList.forEach { screen ->
                        item {
                            GridItem(
                                iconId = screen.iconId,
                                label = screen.title,
                                action = { navController.navigate(screen.route) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GridItem(
    @DrawableRes iconId: Int,
    label: String,
    iconColor: Color = MaterialTheme.colors.primary,
    action: () -> Unit,
    enabled: Boolean = true,
) {
    val textColor = MaterialTheme.colors.onSurface.copy(alpha = LocalContentAlpha.current)
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = action, enabled = enabled),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints {
            val min = Math.min(this.maxHeight.value, this.maxWidth.value) * 0.8
            Image(
                painter = painterResource(id = iconId),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier
                    .size(min.dp)
                    .padding(8.dp)//圆框离中心图标边距
            )
        }

        Text(text = label, textAlign = TextAlign.Center, color = textColor)
    }
}