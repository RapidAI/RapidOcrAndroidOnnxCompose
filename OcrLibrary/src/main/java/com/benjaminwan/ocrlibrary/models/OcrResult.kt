package com.benjaminwan.ocrlibrary.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.opencv.core.Point

open class OcrOutput

object OcrStop : OcrOutput()
object OcrFailed : OcrOutput()

@Parcelize
data class OcrResult(
    val detResults: List<DetResult>,
    val clsResults: List<ClsResult>,
    val recResults: List<RecResult>,
    val boxImg: Bitmap,
) : Parcelable, OcrOutput()

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
    val time: Double,
) : Parcelable

@Parcelize
data class RecResult(
    val text: String, val charScores: List<Float>, val time: Double,
) : Parcelable


