package com.benjaminwan.ocrlibrary

import ai.onnxruntime.OrtEnvironment
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import com.benjaminwan.ocrlibrary.models.OcrResult
import com.benjaminwan.ocrlibrary.models.ScaleParam
import com.orhanobut.logger.Logger
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.android.Utils.matToBitmap
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc.*
import java.io.Closeable
import java.lang.Integer.max

class OcrEngine(context: Context) : Closeable {

    private val assetManager: AssetManager = context.assets

    private val ortEnv by lazy { OrtEnvironment.getEnvironment() }

    private val det by lazy {
        Det(ortEnv, assetManager, DET_NAME)
    }

    private val cls by lazy {
        Cls(ortEnv, assetManager, CLS_NAME)
    }

    private val rec by lazy {
        Rec(ortEnv, assetManager, REC_NAME, KEYS_NAME)
    }

    init {
        if (OpenCVLoader.initDebug()) {
            Logger.i("OpenCV library found inside package.")
        } else {
            Logger.e("Internal OpenCV library not found.")
            throw UnsatisfiedLinkError("Internal OpenCV library not found.")
        }
    }

    override fun close() {
        ortEnv.close()
    }

    fun detect(
        bmp: Bitmap,
        maxSideLen: Int,
        padding: Int,
        boxScoreThresh: Float,
        boxThresh: Float,
        unClipRatio: Float,
        doCls: Boolean,
        mostCls: Boolean
    ): OcrResult {
        Logger.i("padding($padding),maxSideLen($maxSideLen),boxScoreThresh($boxScoreThresh),boxThresh($boxThresh),unClipRatio($unClipRatio),doCls($doCls),mostCls($mostCls)")
        val imgRGBA = Mat(bmp.width, bmp.height, CvType.CV_8UC4)
        Utils.bitmapToMat(bmp, imgRGBA)
        val imgBGR = Mat()
        cvtColor(imgRGBA, imgBGR, COLOR_RGBA2BGR)
        val originMaxSide = max(imgBGR.cols(), imgBGR.rows())
        var resize: Int = if (maxSideLen <= 0 || maxSideLen > originMaxSide) {
            originMaxSide
        } else {
            maxSideLen
        }
        resize += 2 * padding
        val paddingRect = Rect(padding, padding, imgBGR.cols(), imgBGR.rows())
        val paddingSrc = makePadding(imgBGR, padding)
        val s = getScaleParam(paddingSrc, resize)
        //按比例缩小图像，减少文字分割时间
        Logger.i("$s")
        val ocrResult = detect(paddingSrc, paddingRect, s, boxScoreThresh, boxThresh, unClipRatio, doCls, mostCls)

        return ocrResult
    }

    private fun detect(
        src: Mat,
        paddingRect: Rect,
        s: ScaleParam,
        boxScoreThresh: Float,
        boxThresh: Float,
        unClipRatio: Float,
        doCls: Boolean,
        mostCls: Boolean
    ): OcrResult {
        val textBoxPaddingImg = src.clone()
        val thickness = getThickness(src)
        Logger.i("=====Start detect=====")

        Logger.i("---------- step: Get DetResults ----------")
        val detResults = det.getDetResults(src, s, boxScoreThresh, boxThresh, unClipRatio)

        Logger.i("---------- step: Draw TextBoxes ----------")
        drawTextBoxes(textBoxPaddingImg, detResults, thickness)

        Logger.i("---------- step: Get PartMats ----------")
        val partMats = getPartMats(src, detResults)

        val clsResults = if (doCls) {
            Logger.i("---------- step: Get ClsResults ----------")
            val results = cls.getClsResults(partMats)
            if (mostCls) {
                results.map {
                    val sum = results.map { it.index }.sum().toFloat()
                    val halfPercent = results.size.toFloat() / 2.0F
                    //Logger.i("sum=$sum,halfPercent=$halfPercent")
                    val mostAngleIndex = if (sum < halfPercent) 0 else 1
                    it.copy(index = mostAngleIndex)
                }
            } else results
        } else emptyList()

        val clsPartMats = if (doCls) {
            Logger.i("---------- step: Rotate partImages ----------")
            partMats.mapIndexed { index, mat ->
                if (clsResults[index].index == 1) {
                    matRotateClockWise180(mat)
                } else mat
            }
        } else partMats

        Logger.i("---------- step: Get RecResults ----------")
        val recResults = rec.getRecResults(clsPartMats)

        Logger.i("---------- step: Convert BoxImg ----------")
        val outRGBA = Mat()
        cvtColor(textBoxPaddingImg.submat(paddingRect), outRGBA, COLOR_BGR2RGBA)
        val boxImg = Bitmap.createBitmap(
            outRGBA.cols(), outRGBA.rows(), Bitmap.Config.ARGB_8888
        )
        matToBitmap(outRGBA, boxImg)

        /*Logger.i("---------- step: Convert partImages ----------")
        val partImages = partMats.map { partMat ->
            val partMatRGBA = Mat()
            cvtColor(partMat, partMatRGBA, COLOR_BGR2RGBA)
            val partImage = Bitmap.createBitmap(
                partMatRGBA.cols(), partMatRGBA.rows(), Bitmap.Config.ARGB_8888
            )
            matToBitmap(partMatRGBA, partImage)
            partImage
        }*/
        val text = recResults.joinToString(separator = "\n") { it.text }
        return OcrResult(detResults, clsResults, recResults, boxImg, text)
    }


    companion object {
        private const val DET_NAME = "ch_PP-OCRv3_det_infer.onnx"
        private const val CLS_NAME = "ch_ppocr_mobile_v2.0_cls_infer.onnx"
        private const val REC_NAME = "ch_PP-OCRv3_rec_infer.onnx"
        private const val KEYS_NAME = "ppocr_keys_v1.txt"
    }


}