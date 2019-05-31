package com.base.sdk.qrcode.decoding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.base.sdk.qrcode.R
import com.base.sdk.qrcode.camera.CameraManager
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.util.Hashtable

/**
 * author:zack
 * Date:2019/4/22
 * Description:DecodeHandler
 */
class DecodeHandler(private val captureActivityHandler:CaptureActivityHandler,hints:Hashtable<DecodeHintType, Any>)
  :Handler() {

  private val multiFormatReader: MultiFormatReader = MultiFormatReader()

  init {
    multiFormatReader.setHints(hints)
  }

  override fun handleMessage(message: Message) {
    if (message.what == R.id.decode) {
      decode(message.obj as ByteArray, message.arg1, message.arg2)
    } else if (message.what == R.id.quit) {
      Looper.myLooper()?.quit()
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private fun decode(data: ByteArray,width: Int,height: Int) {
    var customWidth = width
    var customHeight = height
    var rawResult: Result? = null

    //modify here
    val rotatedData = ByteArray(data.size)
    for (y in 0 until customHeight) {
      for (x in 0 until customWidth)
        rotatedData[x * customHeight + customHeight - y - 1] = data[x + y * customWidth]
    }
    val tmp = customWidth // Here we are swapping, that's the difference to #11
    customWidth = customHeight
    customHeight = tmp

    val source = CameraManager.get()?.buildLuminanceSource(rotatedData, customWidth, customHeight)
    val bitmap = BinaryBitmap(HybridBinarizer(source))
    try {
      rawResult = multiFormatReader.decodeWithState(bitmap)
    } catch (re: ReaderException) {
      // continue
    } finally {
      multiFormatReader.reset()
    }
    if (rawResult != null) {
      val message = Message.obtain(captureActivityHandler, R.id.decode_succeeded, rawResult)
      val bundle = Bundle()
      bundle.putParcelable("barcode_bitmap", source?.renderCroppedGreyscaleBitmap())
      message.data = bundle
      message.sendToTarget()
    } else {
      val message = Message.obtain(captureActivityHandler, R.id.decode_failed)
      message.sendToTarget()
    }
  }
}