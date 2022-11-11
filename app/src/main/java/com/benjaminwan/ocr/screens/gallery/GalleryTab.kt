package com.benjaminwan.ocr.screens.gallery

import com.benjaminwan.ocr.models.AnyOption

sealed class GalleryTab(
    override val first: Int,
    override val second: String,
) : AnyOption<String> {
    object Picture : GalleryTab(0, "图片")
    object Parameter : GalleryTab(1, "参数")
    object TextResult : GalleryTab(2, "文字结果")
    object BoxImage : GalleryTab(3, "框图")
    object TimeConsumed : GalleryTab(4, "耗时")
    object DetTab : GalleryTab(5, "Det结果")
    object ClsTab : GalleryTab(6, "Cls结果")
    object RecTab : GalleryTab(7, "Rec结果")

    companion object {
        fun getTabs(): List<GalleryTab> = listOf(
            Picture,
            Parameter,
            TextResult,
            BoxImage,
            TimeConsumed,
            DetTab,
            ClsTab,
            RecTab,
        )
    }
}
