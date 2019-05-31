package com.base.sdk.qrcode.view

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.base.sdk.qrcode.R
import com.base.sdk.qrcode.camera.CameraManager
import com.base.sdk.qrcode.decoding.CaptureActivityHandler

/**
 * author:zack
 * Date:2019/4/22
 * Description:QrCodeFragment
 */
open class QrCodeFragment : Fragment(), SurfaceHolder.Callback ,QRCodeCalback {

  private val mHandler = Handler()

  private var captureActivityHandler: CaptureActivityHandler? = null
  private var surfaceView: SurfaceView? = null
  private var viewfinderView:View? = null
  private var hasSurface: Boolean = false
  private var hasScanProcessing = false
  private var callback:QRCodeCalback?=null

  /**
   * 扫码成功
   */
  override fun onScanSuccess(text: String) {
    callback?.onScanSuccess(text)
  }

  /**
   * 扫码失败
   */
  override fun onScanFail() {
    callback?.onScanFail()
  }

  /**
   * 设置扫码callback
   */
  fun setCallback(callback:QRCodeCalback){
    this.callback = callback
  }

  /**
   * 初始化界面
   */
  override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.layout_qrcode_fragment_view, container, false)
    surfaceView = view.findViewById(R.id.id_qrcode_surface)
    viewfinderView = view.findViewById(R.id.id_qrcode_viewfinder)
    val surfaceHolder = surfaceView?.holder
    surfaceHolder?.addCallback(this)
    surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    CameraManager.init(activity!!.application)
    hasSurface = false
    return view
  }

  /**
   * 初始化相机
   */
  fun initCamera() {
    if (hasSurface && checkPermission(android.Manifest.permission.CAMERA)){
      try {
        val surfaceHolder = surfaceView?.holder
        surfaceHolder?.let {
          CameraManager.get()?.openDriver(it)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        return
      }
      if (captureActivityHandler == null) {
        captureActivityHandler = CaptureActivityHandler(this)
      }
      viewfinderView?.postInvalidate()
    }
  }

  /**
   * 延时重新扫描
   */
  fun restartScanDelay(time: Long) {
    hasScanProcessing = false
    mHandler.postDelayed(object :Runnable{
      override fun run() {
        captureActivityHandler?.obtainMessage(R.id.restart_preview)?.sendToTarget()
      }
    }, time)
  }

  /**
   * 打开照明
   */
  fun setTorch(enabled: Boolean) {
    CameraManager.get()?.setTorch(enabled)
  }

  /**
   * 照明是否打开
   */
  fun isTorchOn(): Boolean {
    return CameraManager.get()?.isTorchOn()?:false
  }

  /**
   * 检测应用是否有该权限
   */
  private fun checkPermission(permission: String?): Boolean {
    if (permission == null) return false
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
      context?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }else{
      context?.packageManager?.checkPermission(permission,context?.packageName) == PackageManager.PERMISSION_GRANTED
    }
  }

  /**
   * 保持屏幕常亮
   */
  override fun onStart() {
    super.onStart()
    val window = activity?.window
    window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }

  override fun onResume() {
    super.onResume()
    initCamera()
  }

  override fun onPause() {
    super.onPause()
    captureActivityHandler?.quitSynchronously()
    captureActivityHandler?.removeCallbacksAndMessages(null)
    CameraManager.get()?.closeDriver()
    captureActivityHandler = null
  }

  override fun surfaceChanged(holder: SurfaceHolder,format: Int,width: Int,height: Int) {

  }

  override fun surfaceCreated(holder: SurfaceHolder) {
    if (!hasSurface) {
      hasSurface = true
      initCamera()
    }
  }

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    hasSurface = false
  }

}