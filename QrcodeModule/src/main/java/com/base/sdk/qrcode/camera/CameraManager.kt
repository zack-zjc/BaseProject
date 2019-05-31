package com.base.sdk.qrcode.camera

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.Camera
import android.hardware.Camera.Parameters
import android.os.Build
import android.os.Handler
import android.view.SurfaceHolder
import java.io.IOException

/**
 * author:zack
 * Date:2019/4/22
 * Description:CameraManager
 */
class CameraManager(context: Context) {

  companion object {
    val SDK_INT = Integer.parseInt(Build.VERSION.SDK)

    var cameraManager:CameraManager? = null

    /**
     * Initializes this static object with the Context of the calling Activity.
     * @param pcontext
     * The Activity which wants to use the camera.
     */
    fun init(context: Context) {
      if (cameraManager == null) {
        cameraManager = CameraManager(context)
      }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    fun get(): CameraManager? = cameraManager
  }

  private val configManager: CameraConfigurationManager = CameraConfigurationManager(context)
  private var camera: Camera? = null
  private var framingRect: Rect? = null
  private var framingRectInPreview: Rect? = null
  private var initialized: Boolean = false
  private var previewing: Boolean = false
  /**
   * Preview frames are delivered here, which we pass on to the registered
   * handlerletter. Make sure to clear the handlerletter so it will only receive one
   * message.
   */
  private var previewCallback: PreviewCallback
  /**
   * Autofocus callbacks arrive here, and are dispatched to the Handler which
   * requested them.
   */
  private var autoFocusCallback: AutoFocusCallback? = null

  init {
    previewCallback = PreviewCallback(configManager)
    autoFocusCallback = AutoFocusCallback()
  }

  /**
    * Opens the camera driver and initializes the hardware parameters.
    *
    * @param holder
    * The surface object which the camera will draw preview frames
    * into.
    * @throws IOException
    * Indicates the camera driver failed to open.
  */
	@Throws(IOException::class)
  fun openDriver(holder:SurfaceHolder) {
    if (camera == null){
      camera = Camera.open()
      if (camera == null){
        throw IOException()
      }
      camera?.setPreviewDisplay(holder)
      if (!initialized){
        initialized = true
        configManager.initFromCameraParameters(camera!!)
      }
      configManager.setDesiredCameraParameters(camera!!)
    }
  }

  /**
   * Closes the camera driver if still in use.
   */
  fun closeDriver() {
    if (camera != null)
    {
      camera?.release()
      camera = null
    }
  }

  /**
   * Asks the camera hardware to begin drawing preview frames to the screen.
   */
  fun startPreview() {
    if (camera != null && !previewing)
    {
      camera?.startPreview()
      previewing = true
    }
  }

  /**
   * Tells the camera to stop drawing preview frames.
   */
  fun stopPreview() {
    if (camera != null && previewing)
    {
      camera?.stopPreview()
      previewCallback.setHandler(null, 0)
      autoFocusCallback?.setHandler(null, 0)
      previewing = false
    }
  }

  /**
   * A single preview frame will be returned to the handlerletter supplied. The data
   * will arrive as byte[] in the message.obj field, with width and height
   * encoded as message.arg1 and message.arg2, respectively.
   *
   * @param handlerletter
   * The handlerletter to send the message to.
   * @param message
   * The what field of the message to be sent.
   */
  fun requestPreviewFrame(handler: Handler, message:Int) {
    if (camera != null && previewing)
    {
      previewCallback.setHandler(handler, message)
      camera?.setOneShotPreviewCallback(previewCallback)
    }
  }

  /**
   * Asks the camera hardware to perform an autofocus.
   *
   * @param handlerletter
   * The Handler to notify when the autofocus completes.
   * @param message
   * The message to deliver.
   */
  fun requestAutoFocus(handler:Handler, message:Int) {
    if (camera != null && previewing)
    {
      autoFocusCallback?.setHandler(handler, message)
      camera?.autoFocus(autoFocusCallback)
    }
  }

  /**
   * Calculates the framing rect which the UI should draw to show the user
   * where to place the barcode. This target helps with alignment as well as
   * forces the user to hold the device far enough away to ensure the image
   * will be in focus.
   *
   * @return The rectangle to draw on screen in window coordinates.
   */
  fun getFramingRect():Rect? {
    val screenResolution = configManager.screenResolution
    if (framingRect == null){
      if (camera == null){
        return null
      }
      if (screenResolution == null){
        return null
      }
      val width = screenResolution.x * 3 / 4
      val leftOffset = (screenResolution.x - width) / 2
      val topOffset = (screenResolution.y - width) / 3 //fix from 2 to 3
      framingRect = Rect(leftOffset, topOffset, leftOffset + width,topOffset + width)
    }
    return framingRect
  }

  /**
   * Like [.getFramingRect] but coordinates are in terms of the preview
   * frame, not UI / screen.
   */
  fun getFramingRectInPreview():Rect {
    val rect = Rect(getFramingRect())
    if (framingRectInPreview == null){
      val cameraResolution = configManager.cameraResolution
      val screenResolution = configManager.screenResolution

      if (cameraResolution != null && screenResolution != null){
        rect.left = rect.left * cameraResolution.y / screenResolution.x
        rect.right = rect.right * cameraResolution.y / screenResolution.x
        rect.top = rect.top * cameraResolution.x / screenResolution.y
        rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y
      }
      framingRectInPreview = rect
    }
    return framingRectInPreview!!
  }

  /**
   * A factory method to build the appropriate LuminanceSource object based on
   * the format of the preview buffers, as described by Camera.Parameters.
   *
   * @param data
   * A preview frame.
   * @param width
   * The width of the image.
   * @param height
   * The height of the image.
   * @return A PlanarYUVLuminanceSource instance.
   */
  @Throws(IllegalArgumentException::class)
  fun buildLuminanceSource(data:ByteArray,width:Int, height:Int):PlanarYUVLuminanceSource {
    val rect = getFramingRectInPreview()
    val previewFormat = configManager.previewFormat
    val previewFormatString = configManager.previewFormatString
    when (previewFormat) {
      // This is the standard Android format which all devices are REQUIRED to
      // support.
      // In theory, it's the only one we should ever care about.
      PixelFormat.YCbCr_420_SP,
        // This format has never been seen in the wild, but is compatible as
        // we only care
        // about the Y channel, so allow it.
      PixelFormat.YCbCr_422_SP -> return PlanarYUVLuminanceSource(data, width, height, rect.left,
          rect.top, rect.width(), rect.height())
      else ->
        // The Samsung Moment incorrectly uses this variant instead of the
        // 'sp' version.
        // Fortunately, it too has all the Y data up front, so we can read
        // it.
        if ("yuv420p" == previewFormatString){
          return PlanarYUVLuminanceSource(data, width, height,
              rect.left, rect.top, rect.width(), rect.height())
        }
    }
    throw IllegalArgumentException("Unsupported picture format: "+ previewFormat + '/'.toString() + previewFormatString)
  }

  fun openLight(){ // 打开闪光灯
    if (camera != null) {
      val parameter = camera?.parameters
      parameter?.flashMode = Parameters.FLASH_MODE_TORCH
      camera?.parameters = parameter
    }
  }

  fun closeLight(){ // 关闭闪光灯
    if (camera != null) {
      val parameter = camera?.parameters
      parameter?.flashMode = Parameters.FLASH_MODE_OFF
      camera?.parameters = parameter
    }
  }

  fun setTorch(enabled: Boolean) {
    if (enabled) {
      openLight()
    } else {
      closeLight()
    }
  }

  fun isTorchOn(): Boolean {
    if (camera == null) {
      return false
    }
    val parameter = camera?.parameters
    return Parameters.FLASH_MODE_TORCH == parameter?.flashMode
  }

  /**
   * 自动聚焦结果处理
   */
  internal inner class AutoFocusCallback : Camera.AutoFocusCallback {

    private var autoFocusHandler: Handler? = null
    private var autoFocusMessage: Int = 0

    fun setHandler(autoFocusHandler: Handler?,autoFocusMessage: Int) {
      this.autoFocusHandler = autoFocusHandler
      this.autoFocusMessage = autoFocusMessage
    }

    override fun onAutoFocus(success: Boolean,camera: Camera) {
      if (autoFocusHandler != null) {
        val message = autoFocusHandler?.obtainMessage(autoFocusMessage, success)
        autoFocusHandler?.sendMessageDelayed(message, 1500L)
        autoFocusHandler = null
      }
    }
  }

  /**
   * 聚焦后返回数据处理
   */
  internal inner class PreviewCallback(private val configManager: CameraConfigurationManager) :Camera.PreviewCallback {
    private var previewHandler: Handler? = null
    private var previewMessage: Int = 0

    fun setHandler(previewHandler: Handler?,previewMessage: Int) {
      this.previewHandler = previewHandler
      this.previewMessage = previewMessage
    }

    override fun onPreviewFrame(data: ByteArray,camera: Camera) {
      val cameraResolution = configManager.cameraResolution
      if (previewHandler != null) {
        val message = previewHandler?.obtainMessage(previewMessage, cameraResolution?.x?:0,cameraResolution?.y?:0, data)
        message?.sendToTarget()
        previewHandler = null
      }
    }
  }

}