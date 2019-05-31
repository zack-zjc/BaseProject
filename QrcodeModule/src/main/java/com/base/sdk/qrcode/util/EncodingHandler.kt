package com.base.sdk.qrcode.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.view.Gravity
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.Hashtable

/**
 * author:zack
 * Date:2019/4/22
 * Description:EncodingHandler生成二维码的类
 */
object EncodingHandler {

  private const val BLACK = -0x1000000
  private const val WHITE = -0x1

  /**
   * 生成二维码图图片
   * @param str
   * @param widthAndHeight
   * @return
   */
  fun createQRCode(str: String?,widthAndHeight: Int): Bitmap? {
    try {
      if (str == null || "" == str || str.isEmpty()) {
        return null
      }
      val hints = Hashtable<EncodeHintType, String>()
      hints[EncodeHintType.CHARACTER_SET] = "utf-8"
      val matrix = MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight)
      val width = matrix.width
      val height = matrix.height
      val pixels = IntArray(width * height)
      for (y in 0 until height) {
        for (x in 0 until width) {
          if (matrix.get(x, y)) {
            pixels[y * width + x] = BLACK
          } else {
            pixels[y * width + x] = WHITE
          }
        }
      }
      val bitmap = Bitmap.createBitmap(width, height,Config.ARGB_8888)
      bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
      return bitmap
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  /**
   * 生成条形码
   * @param context
   * @param contents
   * @param desiredWidth
   * @param desiredHeight
   * @param displayCode
   * @return
   */
  fun creatBarcode(context: Context,contents: String,desiredWidth: Int,desiredHeight: Int,displayCode: Boolean): Bitmap? {
    try {
      var rusultBitmap: Bitmap? = null
      val marginW = 20
      val barcodeFormat = BarcodeFormat.CODE_128
      if (displayCode) {
        val barcodeBitmap = encodeAsBitmap(
            contents, barcodeFormat, desiredWidth, desiredHeight
        )
        val codeBitmap = createCodeBitmap(
            contents, desiredWidth + 2 * marginW, desiredHeight, context
        )
        rusultBitmap = mixtureBitmap(
            barcodeBitmap, codeBitmap, PointF(0f, desiredHeight.toFloat())
        )
      } else {
        rusultBitmap = encodeAsBitmap(
            contents, barcodeFormat, desiredWidth, desiredHeight
        )
      }
      return rusultBitmap
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
  }

  private fun encodeAsBitmap(contents: String,format: BarcodeFormat,desiredWidth: Int,desiredHeight: Int): Bitmap? {
    val writer = MultiFormatWriter()
    var result: BitMatrix? = null
    try {
      result = writer.encode(contents, format, desiredWidth, desiredHeight, null)
    } catch (e: WriterException) {
      e.printStackTrace()
    }
    if (result != null) {
      val width = result.width
      val height = result.height
      val pixels = IntArray(width * height)
      // All are 0, or black, by default
      for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
          pixels[offset + x] = if (result.get(x, y)) -0x1000000 else -0x1
        }
      }
      val bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888)
      bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
      return bitmap
    }
    return null
  }

  private fun createCodeBitmap(contents: String,width: Int,height: Int, context: Context): Bitmap {
    val tv = TextView(context)
    val layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    tv.layoutParams = layoutParams
    tv.text = contents
    tv.height = height
    tv.gravity = Gravity.CENTER_HORIZONTAL
    tv.width = width
    tv.isDrawingCacheEnabled = true
    tv.setTextColor(Color.BLACK)
    tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
    tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
    tv.buildDrawingCache()
    val bitmapCode = tv.drawingCache
    tv.destroyDrawingCache()
    return bitmapCode
  }

  private fun mixtureBitmap(first: Bitmap?,second: Bitmap?,fromPoint: PointF?): Bitmap? {
    if (first == null || second == null || fromPoint == null) {
      return null
    }
    val marginW = 20F
    val newBitmap = Bitmap.createBitmap((first.width + second.width + marginW).toInt(),
        first.height + second.height, Config.ARGB_4444)
    val canvas = Canvas(newBitmap)
    canvas.drawBitmap(first, marginW, 0F, null)
    canvas.drawBitmap(second, fromPoint.x, fromPoint.y, null)
    canvas.save()
    return newBitmap
  }

}