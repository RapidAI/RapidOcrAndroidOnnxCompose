package com.benjaminwan.ocr.screens.gallery

import com.benjaminwan.ocr.models.AnyOption

sealed class GalleryTab(
    override val first: Int,
    override val second: String,
) : AnyOption<String> {
    object Picture : GalleryTab(0, "图片")
    object Parameter : GalleryTab(1, "参数")
    object TextResult : GalleryTab(2, "文字结果")
    object BoxImage : GalleryTab(3, "检测结果")
    object DetTab : GalleryTab(4, "Det结果")
    object ClsTab : GalleryTab(5, "Cls结果")
    object RecTab : GalleryTab(6, "Rec结果")

    companion object {
        fun getTabs(): List<GalleryTab> = listOf(
            Picture,
            Parameter,
            TextResult,
            BoxImage,
            DetTab,
            ClsTab,
            RecTab,
        )
    }
}
