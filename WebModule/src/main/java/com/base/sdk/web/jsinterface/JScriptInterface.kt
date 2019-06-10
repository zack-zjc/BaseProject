package com.base.sdk.web.jsinterface

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.fetch.HttpClientUtil
import com.base.sdk.web.R
import com.base.sdk.web.ui.ActBaseWebView
import com.base.sdk.web.ui.ActWebView
import java.lang.ref.WeakReference

/**
 * author:zack
 * Date:2019/3/28
 * Description:js接口类
 */
open class JScriptInterface(activity: Activity) {

  //页面实例
  private val activityReference = WeakReference<Activity>(activity)
  //主线程
  private val mHandler = Handler(Looper.getMainLooper())

  //网页请求
  @JavascriptInterface
  open fun proxyRequest(options: String) {
    val jsonObject = JSONObject.parseObject(options)
    val callbackName = if (jsonObject.containsKey("callback")) jsonObject["callback"] else ""
    jsonObject.remove("callback")
    try{
      val result = HttpClientUtil.fetch(JSONObject.parseObject(options))
      val resultObject = JSONObject.parseObject(result["data"].toString())
      mHandler.post {
        if (activityReference.get() != null && activityReference.get() is ActBaseWebView && !(activityReference.get() as ActBaseWebView).isFinishing){
          (activityReference.get() as ActBaseWebView).getWebView()?.loadUrl("javascript:$callbackName($resultObject);")
        }
      }
    }catch (e:Exception){
      e.printStackTrace()
    }
  }

  /**
   * 分享数据设置
   */
  @JavascriptInterface
  open fun setShareEntity(optionStr: String){
    mHandler.post {
      val options = JSONObject.parseObject(optionStr)
      activityReference.get()?.let {
        if (it is ActBaseWebView) {
          val title = options["title"].toString()
          val desc = options["desc"].toString()
          val image = options["image"].toString()
          val html = options["htmlUrl"].toString()
          it.setShareData(title,desc,image,html)
        }
      }
    }
  }

  /**
   * 网页唤起分享
   */
  @JavascriptInterface
  open fun shareDefault(optionStr: String){
    mHandler.post {
      activityReference.get()?.let{
        if (it is ActBaseWebView) {
          it.shareDefault()
        }
      }
    }
  }

  /**
   *  选择文件
   * json键值对默认选择一个文件，视频不支持裁剪
   * key:type->0:图片拍照 1:图片相册 2：视频
   *     ratioX->裁剪x
   *     ratioY->裁剪Y
   */
  @JavascriptInterface
  open fun pickMedia(optionStr:String){
    mHandler.post {
      activityReference.get()?.let {
        val json = JSONObject.parseObject(optionStr)
        val type = json["type"].toString()
        val callbackName = if (json.containsKey("callback")) json["callback"].toString() else ""
        val ratioX = if (json.containsKey("x")) json["x"].toString().toInt() else 0
        val ratioY = if (json.containsKey("y")) json["y"].toString().toInt() else 0
        if (it is ActWebView){
          when(type){
            "0" -> it.takePhoto(ratioX,ratioY,callbackName)
            "1" -> it.pickImage(ratioX,ratioY,callbackName)
            "2" -> it.pickVideo(callbackName)
          }
        }
      }
    }
  }

  /**
   * 添加到剪切板
   */
  @JavascriptInterface
  fun addToClipboard(message: String) {
    val clipboardManager = activityReference.get()?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    clipboardManager?.text = message
    Toast.makeText(activityReference.get(), R.string.str_web_copy_sucess,Toast.LENGTH_SHORT).show()
  }

  /**
   * 开启扫码页面
   */
  @JavascriptInterface
  open fun ScanQrCode(callbackName: String){
    mHandler.post {
      activityReference.get()?.let{
        if (it is ActBaseWebView) {
          it.startScan(callbackName)
        }
      }
    }
  }

  /**
   * 关闭当前页面
   */
  @JavascriptInterface
  open fun closePage(optionStr: String){
    mHandler.post {
      activityReference.get()?.finish()
    }
  }

}