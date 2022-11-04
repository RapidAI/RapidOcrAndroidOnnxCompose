package com.benjaminwan.ocr.screens.gallery

import com.benjaminwan.ocr.models.AnyOption

sealed class GalleryTab(
    override val first: Int,
    override val second: String,
) : AnyOption<String> {
    object Picture : GalleryTab(0, "图片")
    object Parameter : GalleryTab(1, "参数")
    object Result : GalleryTab(2, "结果")
    object Debug : GalleryTab(3, "调试")

    companion object {
        fun getTabs(): List<GalleryTab> = listOf(
            Picture,
            Parameter,
            Result,
            Debug,
        )
    }
}
