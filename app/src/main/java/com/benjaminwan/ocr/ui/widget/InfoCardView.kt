package com.benjaminwan.ocr.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun InfoCardView(
    modifier: Modifier = Modifier,
    headerText: String,
    infoContent: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(width = 2.dp, color = MaterialTheme.colors.primary)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colors.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = headerText,
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            infoContent()
        }
    }
}

@Composable
fun RowInfoView(
    modifier: Modifier = Modifier,
    header: String,
    content: String,
) {
    Row(
        modifier = modifier
    ) {
        Text(text = header)
        Text(text = content, color = MaterialTheme.colors.primary)
    }
}