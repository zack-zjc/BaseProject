package com.base.sdk.qrcode.view

/**
 * author:zack
 * Date:2019/4/22
 * Description:扫描结果处理
 */
interface QRCodeCalback {

  fun onScanSuccess(text: String)

  fun onScanFail()

}