package com.base.sdk.qrcode.util

import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import com.base.sdk.qrcode.decoding.DecodeFormatManager
import com.base.sdk.qrcode.decoding.RGBLuminanceSource
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.util.Hashtable
import java.util.Vector

/**
 * author:zack
 * Date:2019/4/22
 * Description:DecodeHandler
 */
object DecodeHandler {

  /**
   * 扫描二维码图片的方法
   */
  fun scanningFromImagePath(path: String): String? {
    if (TextUtils.isEmpty(path)) {
      return null
    }
    // 1、解码图片
    val scanBitmap = BitmapFactory.decodeFile(path)
    // 2、RGB亮度
    val source = RGBLuminanceSource(scanBitmap)
    // 3、二值化
    val bitmap = BinaryBitmap(HybridBinarizer(source))
    // 4、读取核心代码
    val multiFormatReader = MultiFormatReader()
    val hints = Hashtable<DecodeHintType, Any>()
    hints[DecodeHintType.CHARACTER_SET] = "UTF8" // 设置二维码内容的编码
    val decodeFormats = Vector<BarcodeFormat>()
    decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS)
    decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
    decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
    decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
    hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
    multiFormatReader.setHints(hints)
    var result: Result? = null
    try {
      result = multiFormatReader.decodeWithState(bitmap)
    } catch (re: ReaderException) {
      re.printStackTrace()
    } finally {
      multiFormatReader.reset()
    }
    return result?.text
  }

}