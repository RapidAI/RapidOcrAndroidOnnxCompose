package com.benjaminwan.ocr.screens.gallery

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Uninitialized
import com.benjaminwan.ocr.base.BaseState
import com.benjaminwan.ocrlibrary.models.OcrResult

data class GalleryState(
    val tabs: List<GalleryTab> = GalleryTab.getTabs(),
    val selectTab: GalleryTab = tabs.first(),
    val detectRequest: Async<OcrResult> = Uninitialized,//打开文件
    val imageUri: Uri? = null,
    val maxSideLen: String = DEFAULT_MAX_SIDE_LEN,
    val maxSideLenError: Boolean = false,
    val padding: String = DEFAULT_PADDING,
    val paddingError: Boolean = false,
    val boxScoreThresh: String = DEFAULT_BOX_SCORE_THRESH,
    val boxScoreThreshError: Boolean = false,
    val boxThresh: String = DEFAULT_BOX_THRESH,
    val boxThreshError: Boolean = false,
    val unClipRatio: String = DEFAULT_UN_CLIP_RATIO,
    val unClipRatioError: Boolean = false,
    val doCls: Boolean = true,
    val mostCls: Boolean = true,
) : BaseState() {
    companion object {
        val IntRange.rangeStr: String
            get() = "${first}~${last}"

        val ClosedFloatingPointRange<Float>.rangeStr: String
            get() = "${start}~${endInclusive}"

        const val DEFAULT_MAX_SIDE_LEN = "1024"
        val maxSideLenRange = 32..Int.MAX_VALUE

        const val DEFAULT_PADDING = "50"
        val paddingRange = 0..Int.MAX_VALUE

        const val DEFAULT_BOX_SCORE_THRESH = "0.5"
        val boxScoreThreshRange = 0f..1f

        const val DEFAULT_BOX_THRESH = "0.3"
        val boxThreshRange = 0f..1f

        const val DEFAULT_UN_CLIP_RATIO = "2.0"
        val unClipRatioRange = 1f..3f

    }
}