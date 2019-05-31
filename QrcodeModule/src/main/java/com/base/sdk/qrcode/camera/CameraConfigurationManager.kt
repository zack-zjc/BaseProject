package com.base.sdk.qrcode.camera

import android.content.Context
import android.graphics.Point
import android.hardware.Camera
import java.util.regex.Pattern
import android.view.WindowManager
import android.os.Build

/**
 * author:zack
 * Date:2019/4/22
 * Description:CameraConfigurationManager
 */
class CameraConfigurationManager(private val context: Context) {

  private val TEN_DESIRED_ZOOM = 27
  private val COMMA_PATTERN = Pattern.compile(",")
  var screenResolution: Point? = null
  var cameraResolution: Point? = null
  var previewFormat: Int = 0
  var previewFormatString: String? = null

  /**
   * Reads, one time, values from the camera that are needed by the app.
   */
  fun initFromCameraParameters(camera: Camera) {
    val parameters = camera.parameters
    previewFormat = parameters.previewFormat
    previewFormatString = parameters.get("preview-format")
    val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = manager.defaultDisplay
    screenResolution = Point(display.width, display.height)
    val screenResolutionForCamera = Point()
    screenResolutionForCamera.x = screenResolution?.x?:0
    screenResolutionForCamera.y = screenResolution?.y?:0
    // preview size is always something like 480*320, other 320*480
    if (screenResolution?.x?:0 < screenResolution?.y?:0) {
      screenResolutionForCamera.x = screenResolution?.y?:0
      screenResolutionForCamera.y = screenResolution?.x?:0
    }
    cameraResolution = getCameraResolution(parameters, screenResolutionForCamera)
  }

  /**
   * Sets the camera up to take preview images which are used for both preview
   * and decoding. We detect the preview format here so that
   * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
   * In the future we may want to force YUV420SP as it's the smallest, and the
   * planar Y can be used for barcode scanning without a copy in some cases.
   */
  fun setDesiredCameraParameters(camera: Camera) {
    val parameters = camera.parameters
    parameters.setPreviewSize(cameraResolution?.x?:0, cameraResolution?.y?:0)
    setFlash(parameters)
    setZoom(parameters)
    // modify here
    camera.setDisplayOrientation(90)
    camera.parameters = parameters
  }

  private fun getCameraResolution(parameters: Camera.Parameters,screenResolution: Point): Point {
    var previewSizeValueString: String? = parameters.get("preview-size-values")
    // saw this on Xperia
    if (previewSizeValueString == null) {
      previewSizeValueString = parameters.get("preview-size-value")
    }
    var cameraResolution: Point? = null
    if (previewSizeValueString != null) {
      cameraResolution = findBestPreviewSizeValue(previewSizeValueString,screenResolution)
    }
    if (cameraResolution == null) {
      // Ensure that the camera resolution is a multiple of 8, as the
      // screen may not be.
      cameraResolution = Point(
          screenResolution.x shr 3 shl 3,
          screenResolution.y shr 3 shl 3
      )
    }
    return cameraResolution
  }

  private fun findBestPreviewSizeValue(previewSizeValueString: CharSequence,screenResolution: Point): Point? {
    var bestX = 0
    var bestY = 0
    var diff = Integer.MAX_VALUE
    for (previewSize in COMMA_PATTERN.split(previewSizeValueString)) {
      val previewSizeTemp = previewSize.trim()
      val dimPosition = previewSizeTemp.indexOf('x')
      if (dimPosition < 0) {
        continue
      }
      val newX: Int
      val newY: Int
      try {
        newX = Integer.parseInt(previewSizeTemp.substring(0, dimPosition))
        newY = Integer.parseInt(previewSizeTemp.substring(dimPosition + 1))
      } catch (nfe: NumberFormatException) {
        continue
      }
      val newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y)
      if (newDiff == 0) {
        bestX = newX
        bestY = newY
        break
      } else if (newDiff < diff) {
        bestX = newX
        bestY = newY
        diff = newDiff
      }
    }
    return if (bestX > 0 && bestY > 0) {
      Point(bestX, bestY)
    } else null
  }

  private fun findBestMotZoomValue(stringValues: CharSequence,tenDesiredZoom: Int): Int {
    var tenBestValue = 0
    for (stringValue in COMMA_PATTERN.split(stringValues)) {
      val stringValueTemp = stringValue.trim { it <= ' ' }
      val value: Double
      try {
        value = java.lang.Double.parseDouble(stringValueTemp)
      } catch (nfe: NumberFormatException) {
        return tenDesiredZoom
      }

      val tenValue = (10.0 * value).toInt()
      if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
        tenBestValue = tenValue
      }
    }
    return tenBestValue
  }

  private fun setFlash(parameters: Camera.Parameters) {
    // FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
    // And this is a hack-hack to work around a different value on the
    // Behold II
    // Restrict Behold II check to Cupcake, per Samsung's advice
    // if (Build.MODEL.contains("Behold II") &&
    // CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
    if (Build.MODEL.contains("Behold II") && CameraManager.Companion.SDK_INT == 3) { // 3
      // Cupcake
      parameters.set("flash-value", 1)
    } else {
      parameters.set("flash-value", 2)
    }
    // This is the standard setting to turn the flash off that all devices
    // should honor.
    parameters.set("flash-mode", "off")
  }

  private fun setZoom(parameters: Camera.Parameters) {
    val zoomSupportedString = parameters.get("zoom-supported")
    if (zoomSupportedString != null && !java.lang.Boolean.parseBoolean(zoomSupportedString)) {
      return
    }
    var tenDesiredZoom = TEN_DESIRED_ZOOM
    val maxZoomString = parameters.get("max-zoom")
    if (maxZoomString != null) {
      try {
        val tenMaxZoom = (10.0 * java.lang.Double
            .parseDouble(maxZoomString)).toInt()
        if (tenDesiredZoom > tenMaxZoom) {
          tenDesiredZoom = tenMaxZoom
        }
      } catch (nfe: NumberFormatException) {
        nfe.printStackTrace()
      }
    }
    val takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max")
    if (takingPictureZoomMaxString != null) {
      try {
        val tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString)
        if (tenDesiredZoom > tenMaxZoom) {
          tenDesiredZoom = tenMaxZoom
        }
      } catch (nfe: NumberFormatException) {
        nfe.printStackTrace()
      }
    }
    val motZoomValuesString = parameters.get("mot-zoom-values")
    if (motZoomValuesString != null) {
      tenDesiredZoom = findBestMotZoomValue(
          motZoomValuesString,
          tenDesiredZoom
      )
    }
    val motZoomStepString = parameters.get("mot-zoom-step")
    if (motZoomStepString != null) {
      try {
        val motZoomStep = java.lang.Double.parseDouble(motZoomStepString.trim())
        val tenZoomStep = (10.0 * motZoomStep).toInt()
        if (tenZoomStep > 1) {
          tenDesiredZoom -= tenDesiredZoom % tenZoomStep
        }
      } catch (nfe: NumberFormatException) {
        // continue
      }
    }
    // Set zoom. This helps encourage the user to pull back.
    // Some devices like the Behold have a zoom parameter
    if (maxZoomString != null || motZoomValuesString != null) {
      parameters.set("zoom", (tenDesiredZoom / 10.0).toString())
    }
    // Most devices, like the Hero, appear to expose this zoom parameter.
    // It takes on values like "27" which appears to mean 2.7x zoom
    if (takingPictureZoomMaxString != null) {
      parameters.set("taking-picture-zoom", tenDesiredZoom)
    }
  }


}