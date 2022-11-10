package com.benjaminwan.ocrlibrary

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import android.content.res.AssetManager
import com.benjaminwan.ocrlibrary.models.ClsResult
import org.opencv.core.CvType.CV_8UC3
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc.resize
import java.util.*

class Cls(private val ortEnv: OrtEnvironment, assetManager: AssetManager, modelName: String) {

    private val session by lazy {
        val model = assetManager.open(modelName, AssetManager.ACCESS_UNKNOWN).readBytes()
        ortEnv.createSession(model)
    }

    private val meanValues = floatArrayOf(127.5F, 127.5F, 127.5F)

    private val normValues = floatArrayOf(1.0F / 127.5F, 1.0F / 127.5F, 1.0F / 127.5F)

    private fun getClsResult(src: Mat): ClsResult {
        val srcResize = adjustToDst(src, DST_WIDTH, DST_HEIGHT)
        val inputTensorValues = substractMeanNormalize(srcResize, meanValues, normValues)
        val inputShape = longArrayOf(1, srcResize.channels().toLong(), srcResize.rows().toLong(), srcResize.cols().toLong())
        val inputName = session.inputNames.iterator().next()
        OnnxTensor.createTensor(ortEnv, inputTensorValues, inputShape).use { inputTensor ->
            session.run(Collections.singletonMap(inputName, inputTensor)).use { output ->
                val onnxValue = output.first().value
                /*val tensorInfo = onnxValue.info as TensorInfo
                val type = onnxValue.type
                Logger.i("info=${tensorInfo},type=$type")*/
                val values = onnxValue.value as Array<FloatArray>
                val outputData = values.flatMap { a ->
                    a.flatMap { listOf(it) }
                }
                val max = outputData.withIndex().maxBy { it.value }
                return ClsResult(max.index, max.value, 0.0)
            }
        }
    }

    fun getClsResults(partMats: List<Mat>, doAngle: Boolean, mostAngle: Boolean): List<ClsResult> {
        val results = partMats.map {
            getClsResult(it)
        }
        return results
    }

    fun adjustToDst(src: Mat, dstWidth: Int, dstHeight: Int): Mat {
        val srcResize = Mat()
        val scale = dstHeight.toDouble() / src.rows()
        val srcWidth = src.cols() * scale
        resize(src, srcResize, Size(srcWidth, dstHeight.toDouble()))
        val srcFit = Mat(dstHeight, dstWidth, CV_8UC3, Scalar(255.0, 255.0, 255.0))
        if (srcWidth < dstWidth) {
            val rect = Rect(0, 0, srcResize.cols(), srcResize.rows())
            srcResize.copyTo(srcFit.submat(rect))
        } else {
            val rect = Rect(0, 0, dstWidth, dstHeight)
            srcResize.submat(rect).copyTo(srcFit)
        }
        return srcFit;
    }

    companion object {
        private const val DST_WIDTH = 192
        private const val DST_HEIGHT = 48
    }

}