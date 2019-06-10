package com.base.sdk.qrcode.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.base.sdk.base.activity.BasePermissionActivity
import com.base.sdk.qrcode.R
import com.base.sdk.qrcode.view.QRCodeCalback
import com.base.sdk.qrcode.view.QrCodeFragment

/**
 * author:zack
 * Date:2019/3/8
 * Description:扫码页面
 */
open class ActQrcodeScanner : BasePermissionActivity(),QRCodeCalback{

  private val fragment = QrCodeFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_qrcode_activity_view)
    findViewById<View>(R.id.id_qrcode_back_view).setOnClickListener {
      finish()
    }
    fragment.setCallback(this)
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(R.id.id_qrcode_view_container,fragment)
    transaction.commit()
  }

  override fun requestedPermission(): Array<String>  = arrayOf(android.Manifest.permission.CAMERA)

  override fun permissionExplainDesc(): CharSequence = getString(R.string.str_qrcode_permission_scanner_hint)

  override fun gainPermissionAfterAction() {
    fragment.restartScanDelay(1000)
  }

  override fun isLightStatusBar(): Boolean = false

  override fun onScanFail() {
    Toast.makeText(this,"无法识别该二维码",Toast.LENGTH_SHORT).show()
  }

  override fun onScanSuccess(text: String) {
    val data = Intent()
    data.putExtra("qrCode",text)
    setResult(Activity.RESULT_OK,data)
    finish()
  }

}