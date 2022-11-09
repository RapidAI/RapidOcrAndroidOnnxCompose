package com.benjaminwan.ocrlibrary

import android.graphics.Bitmap
import androidx.core.math.MathUtils
import com.benjaminwan.ocrlibrary.models.DetPoint
import com.benjaminwan.ocrlibrary.models.DetResult
import com.benjaminwan.ocrlibrary.models.ScaleParam
import de.lighti.clipper.Clipper
import de.lighti.clipper.ClipperOffset
import de.lighti.clipper.Path
import de.lighti.clipper.Paths
import org.opencv.android.Utils.matToBitmap
import org.opencv.core.*
import org.opencv.core.Core.*
import org.opencv.core.CvType.CV_8UC1
import org.opencv.core.Mat.zeros
import org.opencv.imgproc.Imgproc.*
import java.nio.FloatBuffer
import kotlin.math.max


internal fun getScaleParam(src: Mat, targetSize: Int): ScaleParam {
    val srcWidth = src.cols()
    var dstWidth = src.cols()

    val srcHeight = src.rows()
    var dstHeight = src.rows()

    var scale = 1.0F

    if (dstWidth > dstHeight) {
        scale = targetSize.toFloat() / dstWidth.toFloat()
        dstWidth = targetSize
        dstHeight = (dstHeight.toFloat() * scale).toInt()
    } else {
        scale = targetSize.toFloat() / dstHeight.toFloat()
        dstHeight = targetSize
        dstWidth = (dstWidth.toFloat() * scale).toInt()
    }
    if (dstWidth % 32 != 0) {
        dstWidth = (dstWidth / 32 - 1) * 32
        dstWidth = max(dstWidth, 32)
    }
    if (dstHeight % 32 != 0) {
        dstHeight = (dstHeight / 32 - 1) * 32
        dstHeight = max(dstHeight, 32)
    }
    val scaleWidth = dstWidth.toFloat() / srcWidth.toFloat()
    val scaleHeight = dstHeight.toFloat() / srcHeight.toFloat()
    return ScaleParam(
        srcWidth = srcWidth,
        srcHeight = srcHeight,
        dstWidth = dstWidth,
        dstHeight = dstHeight,
        ratioWidth = scaleWidth,
        ratioHeight = scaleHeight,
    )
}

internal fun makePadding(src: Mat, padding: Int): Mat {
    if (padding <= 0) return src
    val paddingScalar = Scalar(255.0, 255.0, 255.0)
    val paddingSrc = Mat()
    Core.copyMakeBorder(src, paddingSrc, padding, padding, padding, padding, Core.BORDER_ISOLATED, paddingScalar)
    return paddingSrc
}

internal fun getThickness(src: Mat): Int {
    val minSize = Math.min(src.cols(), src.rows())
    return minSize / 1000 + 2
}

internal fun substractMeanNormalize(src: Mat, meanVals: FloatArray, normVals: FloatArray): FloatBuffer {
    val inputTensorSize = src.cols() * src.rows() * src.channels()
    val numChannels = src.channels()
    val numCols = src.cols()
    val numRows = src.rows()
    val imageSize = numCols * numRows
    val bitmap = Bitmap.createBitmap(numCols, numRows, Bitmap.Config.ARGB_8888)
    matToBitmap(src, bitmap)
    //Logger.i("bitmap=$bitmap")
    val bmpData = IntArray(imageSize)
    bitmap.getPixels(bmpData, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val imgData = FloatBuffer.allocate(inputTensorSize)
    imgData.rewind()
    for (i in 0 until numCols) {
        for (j in 0 until numRows) {
            val idx = numRows * i + j
            val pixelValue = bmpData[idx]
            /*(0..numChannels).forEach { ch ->
                imgData.put(idx + imageSize * ch, pixelValue * normVals[ch] - meanVals[ch] * normVals[ch])
            }*/
            imgData.put(idx, (((pixelValue shr 16 and 0xFF) / 255f - 0.485f) / 0.229f))
            imgData.put(idx + imageSize, (((pixelValue shr 8 and 0xFF) / 255f - 0.456f) / 0.224f))
            imgData.put(idx + imageSize * 2, (((pixelValue and 0xFF) / 255f - 0.406f) / 0.225f))
        }
    }


    /*Logger.i("inputTensorValues=${inputTensorValues.capacity()}")
    inputTensorValues.rewind()
    (0 until numCols).forEach { col ->
        (0 until numRows).forEach { row ->
            val rgb = src.get(row, col)
            val pid = numRows * col + row
            (0 until numChannels).forEach { ch ->
                val idx = ch * imageSize + pid
                val data = rgb[ch].toFloat() * normVals[ch] - meanVals[ch] * normVals[ch]
                //Logger.i("data[$idx]=${data}")
                inputTensorValues.put(idx, data)
            }
        }
    }*/
    imgData.rewind()
    //Logger.i("inputTensorValues=${imgData.capacity()}")
    return imgData
}

internal fun getMinBoxes(boxRect: RotatedRect, minBoxes: Array<Point>): Float {
    val maxSideLen = max(boxRect.size.width, boxRect.size.height)
    //Logger.i("maxSideLen=$maxSideLen")
    val boxPoint = getBox(boxRect)
    //Logger.i("boxPoint=${boxPoint.contentToString()}")
    boxPoint.sortBy { it.x }
    val index1: Int
    val index2: Int
    val index3: Int
    val index4: Int
    if (boxPoint[1].y > boxPoint[0].y) {
        index1 = 0
        index4 = 1
    } else {
        index1 = 1
        index4 = 0
    }
    if (boxPoint[3].y > boxPoint[2].y) {
        index2 = 2
        index3 = 3
    } else {
        index2 = 3
        index3 = 2
    }

    minBoxes[0] = boxPoint[index1]
    minBoxes[1] = boxPoint[index2]
    minBoxes[2] = boxPoint[index3]
    minBoxes[3] = boxPoint[index4]

    return maxSideLen.toFloat()
}

internal fun getBox(boxRect: RotatedRect): Array<Point> {
    val points: Array<Point> = Array<Point>(4) {
        Point()
    }
    boxRect.points(points)
    return points
}

internal fun boxScoreFast(boxes: Array<Point>, pred: Mat): Float {
    val width = pred.cols()
    val height = pred.rows()
    //Logger.i("width=$width, height=$height")
    val arrayX: Array<Double> = Array(4) { i ->
        boxes[i].x
    }
    val arrayY: Array<Double> = Array(4) { i ->
        boxes[i].y
    }
    //Logger.i("arrayX=${arrayX.contentToString()}, arrayY=${arrayY.contentToString()}")
    val minX = MathUtils.clamp(Math.floor(arrayX.min()).toFloat(), 0.0F, width - 1.0F).toInt()
    val maxX = MathUtils.clamp(Math.ceil(arrayX.max()).toFloat(), 0.0F, width - 1.0F).toInt()
    val minY = MathUtils.clamp(Math.floor(arrayY.min()).toFloat(), 0.0F, height - 1.0F).toInt()
    val maxY = MathUtils.clamp(Math.ceil(arrayY.max()).toFloat(), 0.0F, height - 1.0F).toInt()

    //Logger.i("minX=$minX, maxX=$maxX, minY=$minY, maxY=$maxY")

    val mask = zeros(maxY - minY + 1, maxX - minX + 1, CV_8UC1)

    //Logger.i("mask=$mask")

    val box = arrayOf(
        Point((boxes[0].x.toInt() - minX).toDouble(), (boxes[0].y.toInt() - minY).toDouble()),
        Point((boxes[1].x.toInt() - minX).toDouble(), (boxes[1].y.toInt() - minY).toDouble()),
        Point((boxes[2].x.toInt() - minX).toDouble(), (boxes[2].y.toInt() - minY).toDouble()),
        Point((boxes[3].x.toInt() - minX).toDouble(), (boxes[3].y.toInt() - minY).toDouble())
    )
    //Logger.i("box[0](${box[0].x},${box[0].y}), box[1](${box[1].x},${box[1].y}), box[2](${box[2].x},${box[2].y}), box[3](${box[3].x},${box[3].y})")
    val pts = listOf(MatOfPoint(*box))

    fillPoly(mask, pts, Scalar(1.0))

    val maskData = UByteArray(mask.cols() * mask.rows())
    mask.get(0, 0, maskData)
    var maskSum = 0L
    maskData.forEach {
        maskSum += it.toInt()
    }
    //Logger.i("maskSum=$maskSum")

    //val croppedImg = Mat(pred, Rect(minX, minY, maxX - minX + 1, maxY - minY + 1))
    val croppedImg = pred.submat(Rect(minX, minY, maxX - minX + 1, maxY - minY + 1))

    val score = mean(croppedImg, mask)
    //Logger.i("score=$score")
    return score.`val`[0].toFloat()
}

internal fun unClip(box: Array<Point>, unClipRatio: Float): RotatedRect {
    val distance = getContourArea(box, unClipRatio)
    val path = Path().apply {
        addAll(box.map { de.lighti.clipper.Point.LongPoint(it.x.toLong(), it.y.toLong()) })
    }
    val offset = ClipperOffset()
    offset.addPath(path, Clipper.JoinType.ROUND, Clipper.EndType.CLOSED_POLYGON)
    val soln = Paths()
    offset.execute(soln, distance.toDouble())

    val points = soln.flatten().map { Point(it.x.toDouble(), it.y.toDouble()) }.toTypedArray()

    return if (points.isEmpty()) {
        RotatedRect(Point(0.0, 0.0), Size(1.0, 1.0), 0.0)
    } else {
        minAreaRect(MatOfPoint2f(*points))
    }
}

internal fun getContourArea(box: Array<Point>, unClipRatio: Float): Float {
    val size = box.size
    var area = 0.0
    var dist = 0.0
    for (i in 0 until size) {
        area += box[i].x * box[(i + 1) % size].y -
                box[i].y * box[(i + 1) % size].x
        dist += Math.sqrt(
            (box[i].x - box[(i + 1) % size].x) *
                    (box[i].x - box[(i + 1) % size].x) +
                    (box[i].y - box[(i + 1) % size].y) *
                    (box[i].y - box[(i + 1) % size].y)
        )
    }
    area = Math.abs(area / 2.0)

    return area.toFloat() * unClipRatio / dist.toFloat()
}

internal fun drawTextBoxes(boxImg: Mat, textBoxes: List<DetResult>, thickness: Int) {
    val color = Scalar(0.0, 0.0, 255.0)// B(0) G(0) R(255)
    textBoxes.filter { it.points.size == 4 }.forEach { box ->
        line(boxImg, box.points[0].toCvPoint(), box.points[1].toCvPoint(), color, thickness)
        line(boxImg, box.points[1].toCvPoint(), box.points[2].toCvPoint(), color, thickness)
        line(boxImg, box.points[2].toCvPoint(), box.points[3].toCvPoint(), color, thickness)
        line(boxImg, box.points[3].toCvPoint(), box.points[0].toCvPoint(), color, thickness)
    }
}


internal fun getRotateCropImage(src: Mat, box: List<DetPoint>): Mat {
    val points = box.map { it.toCvPoint() }

    val collectX = arrayOf(box[0].x, box[1].x, box[2].x, box[3].x)
    val collectY = arrayOf(box[0].y, box[1].y, box[2].y, box[3].y)
    val left = collectX.min()
    val right = collectX.max()
    val top = collectY.min()
    val bottom = collectY.max()

    val imgCrop = src.submat(Rect(left, top, right - left, bottom - top))

    for (i in points.indices) {
        points[i].x -= left
        points[i].y -= top
    }

    val imgCropWidth = Math.sqrt(
        Math.pow(points[0].x - points[1].x, 2.0) + Math.pow(points[0].y - points[1].y, 2.0)
    )

    val imgCropHeight = Math.sqrt(
        Math.pow(points[0].x - points[3].x, 2.0) + Math.pow(points[0].y - points[3].y, 2.0)
    )

    val ptsDst = arrayOf(
        Point(0.0, 0.0),
        Point(imgCropWidth, 0.0),
        Point(imgCropWidth, imgCropHeight),
        Point(0.0, imgCropHeight)
    )

    val ptsSrc = arrayOf(
        Point(points[0].x, points[0].y),
        Point(points[1].x, points[1].y),
        Point(points[2].x, points[2].y),
        Point(points[3].x, points[3].y),
    )
    val transformation = getPerspectiveTransform(MatOfPoint2f(*ptsSrc), MatOfPoint2f(*ptsDst))

    val partImg = Mat()
    warpPerspective(
        imgCrop, partImg, transformation,
        Size(imgCropWidth, imgCropHeight),
        BORDER_REPLICATE
    );

    return if (partImg.rows() >= partImg.cols() * 1.5) {
        val srcCopy = Mat(partImg.rows(), partImg.cols(), partImg.depth())
        transpose(partImg, srcCopy)
        flip(srcCopy, srcCopy, 0)
        srcCopy
    } else {
        partImg
    }
}

internal fun getPartMats(src: Mat, detResults: List<DetResult>): List<Mat> {
    return detResults.map {
        getRotateCropImage(src, it.points)
    }
}