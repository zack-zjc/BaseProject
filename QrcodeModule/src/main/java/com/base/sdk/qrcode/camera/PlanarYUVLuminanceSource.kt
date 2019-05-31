package com.base.sdk.qrcode.camera

import com.google.zxing.LuminanceSource
import android.graphics.Bitmap
import kotlin.experimental.and

/**
 * author:zack
 * Date:2019/4/22
 * Description:PlanarYUVLuminanceSource
 */
class PlanarYUVLuminanceSource(private val yuvData:ByteArray,private val dataWidth:Int,private val dataHeight:Int,private val left:Int,
  private val top:Int,CustomWidth:Int,CustomHeight:Int):LuminanceSource(CustomWidth,CustomHeight) {


  init {
    if (left + width > dataWidth || top + height > dataHeight) {
      throw IllegalArgumentException("Crop rectangle does not fit within image data.")
    }
  }


  override fun getRow(y: Int,row: ByteArray?): ByteArray? {
    if (y < 0 || y >= height) {
      throw IllegalArgumentException("Requested row is outside the image: $y")
    }
    val width = width
    var customRow :ByteArray? = row
    if (customRow == null || customRow.size < width) {
      customRow = ByteArray(width)
    }
    val offset = (y + top) * dataWidth + left
    System.arraycopy(yuvData, offset, customRow, 0, width)
    return customRow
  }

  override fun getMatrix(): ByteArray {
    val width = width
    val height = height
    // If the caller asks for the entire underlying image, save the copy and give them the
    // original data. The docs specifically warn that result.length must be ignored.
    if (width == dataWidth && height == dataHeight) {
      return yuvData
    }
    val area = width * height
    val matrix = ByteArray(area)
    var inputOffset = top * dataWidth + left
    // If the width matches the full width of the underlying data, perform a single copy.
    if (width == dataWidth) {
      System.arraycopy(yuvData, inputOffset, matrix, 0, area)
      return matrix
    }
    // Otherwise copy one cropped row at a time.
    val yuv = yuvData
    for (y in 0 until height) {
      val outputOffset = y * width
      System.arraycopy(yuv, inputOffset, matrix, outputOffset, width)
      inputOffset += dataWidth
    }
    return matrix
  }

  override fun isCropSupported(): Boolean = true

  fun renderCroppedGreyscaleBitmap(): Bitmap {
    val width = width
    val height = height
    val pixels = IntArray(width * height)
    val yuv = yuvData
    var inputOffset = top * dataWidth + left

    for (y in 0 until height) {
      val outputOffset = y * width
      for (x in 0 until width) {
        val grey = yuv[inputOffset + x] and 0xff.toByte()
        pixels[outputOffset + x] = -0x1000000 or grey * 0x00010101
      }
      inputOffset += dataWidth
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
  }

}