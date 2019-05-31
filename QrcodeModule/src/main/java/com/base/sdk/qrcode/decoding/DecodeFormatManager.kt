package com.base.sdk.qrcode.decoding

import com.google.zxing.BarcodeFormat
import java.util.Vector

/**
 * author:zack
 * Date:2019/4/22
 * Description:DecodeFormatManager
 */
object DecodeFormatManager {

  var PRODUCT_FORMATS: Vector<BarcodeFormat> = Vector()
  var ONE_D_FORMATS: Vector<BarcodeFormat> = Vector()
  var QR_CODE_FORMATS: Vector<BarcodeFormat> = Vector()
  var DATA_MATRIX_FORMATS: Vector<BarcodeFormat> = Vector()

  init{
    PRODUCT_FORMATS.add(BarcodeFormat.UPC_A)
    PRODUCT_FORMATS.add(BarcodeFormat.UPC_E)
    PRODUCT_FORMATS.add(BarcodeFormat.EAN_13)
    PRODUCT_FORMATS.add(BarcodeFormat.EAN_8)
    PRODUCT_FORMATS.add(BarcodeFormat.RSS_14)
    ONE_D_FORMATS.addAll(PRODUCT_FORMATS)
    ONE_D_FORMATS.add(BarcodeFormat.CODE_39)
    ONE_D_FORMATS.add(BarcodeFormat.CODE_93)
    ONE_D_FORMATS.add(BarcodeFormat.CODE_128)
    ONE_D_FORMATS.add(BarcodeFormat.ITF)
    QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE)
    DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX)
  }

}