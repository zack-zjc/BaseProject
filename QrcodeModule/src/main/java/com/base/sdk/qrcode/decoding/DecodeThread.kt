package com.base.sdk.qrcode.decoding

import android.os.Handler
import android.os.Looper
import com.google.zxing.DecodeHintType
import java.util.Hashtable
import java.util.Vector
import java.util.concurrent.CountDownLatch

/**
 * author:zack
 * Date:2019/4/22
 * Description:DecodeThread
 */
class DecodeThread(private val captureActivityHandler:CaptureActivityHandler) : Thread() {

  private var hints = Hashtable<DecodeHintType, Any>(3)
  private var handler: Handler? = null
  private var handlerInitLatch: CountDownLatch = CountDownLatch(1)

  init {
    val decodeFormats = Vector<Any>()
    decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
    decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
    decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
    hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
  }

  fun getHandler(): Handler? {
    try {
      handlerInitLatch.await()
    } catch (ie: InterruptedException) {
      ie.printStackTrace()
    }
    return handler
  }

  override fun run() {
    Looper.prepare()
    handler = DecodeHandler(captureActivityHandler, hints)
    handlerInitLatch.countDown()
    Looper.loop()
  }

}