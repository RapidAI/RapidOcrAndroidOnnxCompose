package com.benjaminwan.ocrlibrary

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.TensorInfo
import android.content.res.AssetManager
import com.benjaminwan.ocrlibrary.models.DetPoint
import com.benjaminwan.ocrlibrary.models.DetResult
import com.benjaminwan.ocrlibrary.models.ScaleParam
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*

class Det(private val ortEnv: OrtEnvironment, assetManager: AssetManager, modelName: String) {

    private val session by lazy {
        val model = assetManager.open(modelName, AssetManager.ACCESS_UNKNOWN).readBytes()
        ortEnv.createSession(model)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun getDetResults(src: Mat, s: ScaleParam, boxScoreThresh: Float, boxThresh: Float, unClipRatio: Float): List<DetResult> {
        val srcResize = Mat()
        Imgproc.resize(src, srcResize, Size(s.dstWidth.toDouble(), s.dstHeight.toDouble()))

        val inputTensorValues = substractMeanNormalize(srcResize, meanValues, normValues)
        val inputShape = longArrayOf(1, srcResize.channels().toLong(), srcResize.rows().toLong(), srcResize.cols().toLong())
        val inputName = session.inputNames.iterator().next()

        OnnxTensor.createTensor(ortEnv, inputTensorValues, inputShape).use { inputTensor ->
            session.run(Collections.singletonMap(inputName, inputTensor)).use { output ->
                val onnxValue = output.first().value
                val tensorInfo = onnxValue.info as TensorInfo
                /*val type = onnxValue.type
                Logger.i("info=${tensorInfo},type=$type")*/
                val values = onnxValue.value as Array<Array<Array<FloatArray>>>
                val outputData = values.flatMap { a ->
                    a.flatMap { b ->
                        b.flatMap { c ->
                            c.flatMap {
                                listOf(it)
                            }
                        }
                    }
                }
                val outHeight: Int = tensorInfo.shape[2].toInt()
                val outWidth: Int = tensorInfo.shape[3].toInt()
                //-----Data preparation-----
                val predData = outputData.toFloatArray()
                val cbufData = outputData.map { (it * 255).toInt().toUByte() }.toUByteArray()

                val predMat = Mat(outHeight, outWidth, CvType.CV_32F)
                predMat.put(0, 0, predData)

                val cBufMat = Mat(outHeight, outWidth, CvType.CV_8UC1)
                cBufMat.put(0, 0, cbufData)

                //-----boxThresh-----
                val thresholdMat = Mat()
                Imgproc.threshold(cBufMat, thresholdMat, boxThresh * 255.0, 255.0, Imgproc.THRESH_BINARY)

                //-----dilate-----
                val dilateMat = Mat()
                val dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0, 2.0))
                Imgproc.dilate(thresholdMat, dilateMat, dilateElement)

                return findRsBoxes(predMat, dilateMat, s, boxScoreThresh, unClipRatio)
            }
        }

    }

    private fun findRsBoxes(predMat: Mat, dilateMat: Mat, s: ScaleParam, boxScoreThresh: Float, unClipRatio: Float): List<DetResult> {
        val longSideThresh = 3//minBox 长边门限
        val maxCandidates = 1000

        val contours: MutableList<MatOfPoint> = mutableListOf()
        val hierarchy = Mat()

        Imgproc.findContours(dilateMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

        val numContours = if (contours.size >= maxCandidates) maxCandidates else contours.size
        //Logger.i("numContours=$numContours")
        val rsBoxes: MutableList<DetResult> = mutableListOf()

        for (i in (0 until numContours)) {
            //Logger.i("contours[$i]=${contours[i]}}")
            //Logger.i("total=${contours[i].total()},elemSize=${contours[i].elemSize()}")
            if (contours[i].elemSize() <= 2) {
                continue
            }
            val minAreaRect = Imgproc.minAreaRect(MatOfPoint2f(*contours[i].toArray()))
            //Logger.i("minAreaRect[$i]=${minAreaRect}")
            val minBoxes: Array<Point> = Array(4) {
                Point()
            }
            //Logger.i("minBoxes1=${minBoxes.contentToString()}")
            val longSide: Float = getMinBoxes(minAreaRect, minBoxes)
            //Logger.i("longSide[$i]=$longSide")
            //Logger.i("minBoxes[$i]=${minBoxes.contentToString()}")
            if (longSide < longSideThresh) {
                continue
            }

            val boxScore: Float = boxScoreFast(minBoxes, predMat)
            //Logger.i("boxScore[$i]=${boxScore}")
            if (boxScore < boxScoreThresh)
                continue
            //-----unClip-----
            val clipRect: RotatedRect = unClip(minBoxes, unClipRatio)
            //Logger.i("clipRect[$i]=${clipRect}")
            if (clipRect.size.height < 1.001 && clipRect.size.width < 1.001) {
                continue
            }
            //-----unClip-----
            val clipMinBoxes: Array<Point> = Array(4) {
                Point()
            }
            val clipLongSide = getMinBoxes(clipRect, clipMinBoxes)
            //Logger.i("clipLongSide[$i]=$clipLongSide")
            //Logger.i("clipMinBoxes[$i]=${clipMinBoxes.contentToString()}")
            if (clipLongSide < longSideThresh + 2)
                continue

            val intClipMinBoxes = clipMinBoxes.map { point ->
                val x = point.x / s.ratioWidth
                val y = point.y / s.ratioHeight
                val ptX = Math.min(Math.max(x.toInt(), 0), s.srcWidth - 1)
                val ptY = Math.min(Math.max(y.toInt(), 0), s.srcHeight - 1)
                DetPoint(ptX, ptY)
            }
            rsBoxes.add(DetResult(intClipMinBoxes, boxScore))
            //Logger.i("rsBoxes[$i]=${rsBoxes[i]}")
        }
        return rsBoxes.asReversed()
    }

    companion object {
        private val meanValues = floatArrayOf(0.485F * 255, 0.456F * 255, 0.406F * 255)

        private val normValues = floatArrayOf(1.0F / 0.229F / 255.0F, 1.0F / 0.224F / 255.0F, 1.0F / 0.225F / 255.0F)
    }

}