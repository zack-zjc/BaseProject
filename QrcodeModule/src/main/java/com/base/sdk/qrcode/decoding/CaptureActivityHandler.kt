package com.base.sdk.qrcode.decoding

import android.os.Handler
import android.os.Message
import com.base.sdk.qrcode.R
import com.base.sdk.qrcode.camera.CameraManager
import com.base.sdk.qrcode.view.QRCodeCalback
import com.google.zxing.Result



/**
 * author:zack
 * Date:2019/4/22
 * Description:CaptureActivityHandler
 */
class CaptureActivityHandler(private val qrCodeCallback: QRCodeCalback?): Handler() {

  private val decodeThread: DecodeThread = DecodeThread(this)
  private var state: State = State.SUCCESS

  private enum class State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  init {
    decodeThread.start()
    CameraManager.get()?.startPreview()
    restartPreviewAndDecode()
  }

  override fun handleMessage(message: Message) {
    if (message.what == R.id.auto_focus) {
      // When one auto focus pass finishes, start another. This is the closest thing to
      // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
      if (state === State.PREVIEW) {
        CameraManager.get()?.requestAutoFocus(this, R.id.auto_focus)
      }
    } else if (message.what == R.id.restart_preview) {
      restartPreviewAndDecode()
    } else if (message.what == R.id.decode_succeeded) {
      state = State.SUCCESS
      qrCodeCallback?.onScanSuccess((message.obj as Result).text)
    } else if (message.what == R.id.decode_failed) {
      // We're decoding as fast as possible, so when one decode fails, start another.
      state = State.PREVIEW
      decodeThread.getHandler()?.let {
        CameraManager.get()?.requestPreviewFrame(it, R.id.decode)
      }
    }
  }

  fun quitSynchronously() {
    state = State.DONE
    CameraManager.get()?.stopPreview()
    val quit = Message.obtain(decodeThread.getHandler(), R.id.quit)
    quit.sendToTarget()
    try {
      decodeThread.join()
    } catch (e: InterruptedException) {
      // continue
    }
    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded)
    removeMessages(R.id.decode_failed)
  }

  private fun restartPreviewAndDecode() {
    if (state === State.SUCCESS) {
      state = State.PREVIEW
      decodeThread.getHandler()?.let {
        CameraManager.get()?.requestPreviewFrame(it, R.id.decode)
        CameraManager.get()?.requestAutoFocus(this, R.id.auto_focus)
      }
    }
  }

}