package com.benjaminwan.ocrlibrary.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.opencv.core.Point

@Parcelize
data class OcrResult(
    val detResults: List<DetResult>,
    val clsResults: List<ClsResult>,
    val clsTime: Double,
    val recResults: List<RecResult>,
    val detTime: Double,
    val recTime: Double,
    val fullTime: Double,
    val boxImage: Bitmap,
    val text: String,
) : Parcelable

@Parcelize
data class DetPoint(var x: Int, var y: Int) : Parcelable {
    fun toCvPoint() = Point(x.toDouble(), y.toDouble())
}

@Parcelize
data class DetResult(
    val points: List<DetPoint> = listOf(),
    val score: Float,
) : Parcelable

@Parcelize
data class ClsResult(
    val index: Int,
    val score: Float,
) : Parcelable {
    val indexDirection: String get() = if (index == 0) "↑" else "↓"
}

@Parcelize
data class RecResult(
    val text: String,
    val charScores: List<Float>,
) : Parcelable


