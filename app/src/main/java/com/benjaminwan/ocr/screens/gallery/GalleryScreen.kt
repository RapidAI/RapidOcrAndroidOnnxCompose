package com.benjaminwan.ocr.screens.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.benjaminwan.ocr.screens.CommonScaffold
import com.benjaminwan.ocr.screens.NavBackIcon
import com.benjaminwan.ocr.screens.Screen
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.DEFAULT_BOX_SCORE_THRESH
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.DEFAULT_BOX_THRESH
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.DEFAULT_MAX_SIDE_LEN
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.DEFAULT_PADDING
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.DEFAULT_UN_CLIP_RATIO
import com.benjaminwan.ocr.ui.widget.InfoCardView
import com.benjaminwan.ocr.ui.widget.RowInfoView

@Composable
fun GalleryScreen(navController: NavHostController) {
    val vm: GalleryViewModel = mavericksViewModel()
    val state by vm.collectAsState()
    CommonScaffold(
        modifier = Modifier.fillMaxSize(),
        title = { Text(text = stringResource(id = Screen.Gallery.titleId)) },
        navigationIcon = { NavBackIcon(navController) },
        scaffoldState = state.scaffoldState,
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            HeaderView(vm = vm, state = state)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .border(width = 2.dp, color = Color.Gray)
            ) {
                when (state.selectTab) {
                    GalleryTab.Picture -> PictureView(state = state)
                    GalleryTab.Parameter -> ParamView(vm = vm, state = state)
                    GalleryTab.TextResult -> TextResultView(vm = vm, state = state)
                    GalleryTab.BoxImage -> BoxImageView(state = state)
                    GalleryTab.DetTab -> DetResultsView(state = state)
                    GalleryTab.ClsTab -> ClsResultsView(state = state)
                    GalleryTab.RecTab -> RecResultsView(state = state)
                }
            }
            BottomButton(vm = vm, state = state)
        }
    }
}

@Composable
private fun HeaderView(vm: GalleryViewModel, state: GalleryState) {
    ScrollableTabRow(selectedTabIndex = state.selectTab.first) {
        state.tabs.filterIndexed { index, galleryTab ->
            when {
                state.imageUri == null -> index < 1
                state.detectRequest !is Success -> index < 2
                else -> true
            }
        }.forEach {
            Tab(selected = it.first == state.selectTab.first, onClick = { vm.setSelectTab(it) }) {
                Text(
                    text = it.second,
                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun BottomButton(vm: GalleryViewModel, state: GalleryState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val actionOpenPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) vm.setUri(uri)
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            onClick = {
                actionOpenPicker.launch("image/*")
            },
            enabled = state.detectRequest !is Loading,
        ) {
            Text(text = "打开")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            onClick = { vm.detect() },
            enabled = state.detectRequest !is Loading,
        ) {
            Text(text = "识别")
        }
    }
}

@Composable
private fun PictureView(state: GalleryState) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val minSize = min(this.maxWidth, this.maxHeight)
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = state.imageUri).build()
            ), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit
        )
        AnimatedVisibility(visible = state.detectRequest is Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(minSize), color = MaterialTheme.colors.primary.copy(alpha = 0.5f), strokeWidth = minSize / 10
            )
        }
    }
}

@Composable
private fun ParamView(vm: GalleryViewModel, state: GalleryState) {
    val editEnabled = state.detectRequest !is Loading
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(4.dp),
    ) {
        item {
            Column {
                Text(text = "maxSideLen，以长边缩放，单位像素,范围32~1024,默认${DEFAULT_MAX_SIDE_LEN}", color = MaterialTheme.colors.primary)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.maxSideLen,
                    onValueChange = { vm.setMaxSideLen(it) },
                    keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number),
                    isError = state.maxSideLenError,
                    singleLine = true,
                    enabled = editEnabled,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Padding，图像加白边，单位像素，范围0~${Int.MAX_VALUE},默认${DEFAULT_PADDING}", color = MaterialTheme.colors.primary)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.padding,
                    onValueChange = { vm.setPadding(it) },
                    keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number),
                    isError = state.paddingError,
                    singleLine = true,
                    enabled = editEnabled,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "boxScoreThresh，框置信度门限，范围0~1.0，默认${DEFAULT_BOX_SCORE_THRESH}", color = MaterialTheme.colors.primary)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.boxScoreThresh,
                    onValueChange = { vm.setBoxScoreThresh(it) },
                    keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Decimal),
                    isError = state.boxScoreThreshError,
                    singleLine = true,
                    enabled = editEnabled,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "boxThresh，过滤噪点，范围0~1.0，默认${DEFAULT_BOX_THRESH}", color = MaterialTheme.colors.primary)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.boxThresh,
                    onValueChange = { vm.setBoxThresh(it) },
                    keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Decimal),
                    isError = state.boxThreshError,
                    singleLine = true,
                    enabled = editEnabled,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "unClipRatio，框大小倍率，范围1.0~3.0，默认${DEFAULT_UN_CLIP_RATIO}", color = MaterialTheme.colors.primary)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.unClipRatio,
                    onValueChange = { vm.setUnClipRatio(it) },
                    keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Decimal),
                    isError = state.unClipRatioError,
                    singleLine = true,
                    enabled = editEnabled,
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "文字方向检测，当投票关闭时，针对每行检测方向，默认:开", color = MaterialTheme.colors.primary)
                Switch(checked = state.doAngle, onCheckedChange = { vm.setDoAngle(it) }, enabled = editEnabled)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "文字方向投票(以最大概率作为全文方向)，默认:开", color = MaterialTheme.colors.primary)
                Switch(checked = state.mostAngle, onCheckedChange = { vm.setMostAngle(it) }, enabled = editEnabled)

            }
        }
    }
}

@Composable
private fun TextResultView(vm: GalleryViewModel, state: GalleryState) {
    if (state.detectRequest is Success) {
        val text = state.detectRequest()?.text ?: ""
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        text = text,
                    )
                }
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = { vm.toClipboard(text) }) {
                Text(text = "复制到剪切板")
            }
        }
    }
}

@Composable
private fun BoxImageView(state: GalleryState) {
    if (state.detectRequest is Success) {
        val boxImage = state.detectRequest()?.boxImage
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = boxImage).build()
            ), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit
        )
    }
}


@Composable
private fun DetResultsView(state: GalleryState) {
    if (state.detectRequest is Success) {
        val detResults = state.detectRequest()?.detResults ?: emptyList()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            detResults.forEachIndexed { index, detResult ->
                item {
                    InfoCardView(headerText = "框${index}") {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "坐标:",
                                content = detResult.points.map { "(${it.x},${it.y})" }.joinToString()
                            )
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "置信度:",
                                content = "${detResult.score}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClsResultsView(state: GalleryState) {
    if (state.detectRequest is Success) {
        val clsResults = state.detectRequest()?.clsResults ?: emptyList()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            clsResults.forEachIndexed { index, clsResult ->
                item {
                    InfoCardView(
                        headerText = "框${index}",
                    ) {
                        Column {
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "方向:",
                                content = clsResult.indexDirection
                            )
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "置信度:",
                                content = clsResult.score.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecResultsView(state: GalleryState) {
    if (state.detectRequest is Success) {
        val recResults = state.detectRequest()?.recResults ?: emptyList()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            recResults.forEachIndexed { index, recResult ->
                item {
                    InfoCardView(headerText = "框${index}") {
                        Column {
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "文字:",
                                content = recResult.text
                            )
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "置信度:",
                                content = recResult.charScores.toString()
                            )
                            RowInfoView(
                                modifier = Modifier.fillMaxWidth(),
                                header = "耗时:",
                                content = recResult.time.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}