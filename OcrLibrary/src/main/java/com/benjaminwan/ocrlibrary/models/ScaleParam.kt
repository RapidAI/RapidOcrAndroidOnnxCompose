package com.benjaminwan.ocrlibrary.models

data class ScaleParam(
    val srcWidth: Int,
    val srcHeight: Int,
    val dstWidth: Int,
    val dstHeight: Int,
    val ratioWidth: Float,
    val ratioHeight: Float,
)
