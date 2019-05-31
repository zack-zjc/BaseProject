package com.base.sdk.qrcode.decoding

import android.graphics.Bitmap
import com.google.zxing.LuminanceSource

/**
 * author:zack
 * Date:2019/4/22
 * Description:RGBLuminanceSource
 */
class RGBLuminanceSource(bitmap: Bitmap) : LuminanceSource(bitmap.width,bitmap.height) {

  private lateinit var luminances: ByteArray

  init {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    // In order to measure pure decoding speed, we convert the entire image
    // to a greyscale array
    // up front, which is the same as the Y channel of the
    // YUVLuminanceSource in the real app.
    luminances = ByteArray(width * height)
    for (y in 0 until height) {
      val offset = y * width
      for (x in 0 until width) {
        val pixel = pixels[offset + x]
        val r = pixel shr 16 and 0xff
        val g = pixel shr 8 and 0xff
        val b = pixel and 0xff
        if (r == g && g == b) {
          // Image is already greyscale, so pick any channel.
          luminances[offset + x] = r.toByte()
        } else {
          // Calculate luminance cheaply, favoring green.
          luminances[offset + x] = (r + g + g + b shr 2).toByte()
        }
      }
    }
  }

  @Throws(IllegalArgumentException::class)
  override fun getRow(y: Int,row: ByteArray?): ByteArray {
    if (y < 0 || y >= height) {
      throw IllegalArgumentException("Requested row is outside the image: $y")
    }
    val width = width
    var customRow :ByteArray? = row
    if (customRow == null || customRow.size < width) {
      customRow = ByteArray(width)
    }
    System.arraycopy(luminances, y * width, customRow, 0, width)
    return customRow
  }

  override fun getMatrix(): ByteArray = luminances

}