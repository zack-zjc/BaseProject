package com.base.sdk.weex.module

import com.base.sdk.weex.ui.ActBaseWeexPage
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule

/**
 * author:zack
 * Date:2019/3/27
 * Description:事件处理module
 */
open class WXLCEventModule : WXModule(){

  /**
   * 选择文件
   * json键值对默认选择一个文件，视频不支持裁剪
   * key:type->0:图片拍照 1:图片相册 2：视频
   *     ratioX->裁剪x
   *     ratioY->裁剪Y
   */
  @JSMethod(uiThread = true)
  open fun pickMedia(option: Map<String,Any>,callback: JSCallback?){
    val type = option["type"].toString()
    val ratioX = if (option.containsKey("x")) option["x"].toString().toInt() else 0
    val ratioY = if (option.containsKey("y")) option["y"].toString().toInt() else 0
    val context = mWXSDKInstance.context
    if (context is ActBaseWeexPage) {
      callback?.let {
        when(type){
          "0" -> context.takePhoto(ratioX,ratioY,it)
          "1" -> context.pickImage(ratioX,ratioY,it)
          "2" -> context.pickVideo(it)
        }
      }
    }
  }

}