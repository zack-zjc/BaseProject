package com.base.sdk.web.ui.client

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.base.sdk.web.constant.WebConstants
import java.io.File
import java.io.FileInputStream

/**
 * author:zack
 * Date:2019/6/11
 * Description:基础的webClient
 */
open class BaseWebClient : WebViewClient() {

  /**
   * 自定义加载url
   */
  override fun shouldOverrideUrlLoading(view: WebView?,request: WebResourceRequest?): Boolean {
    if(request == null) return false
    val url = request.url.toString()
    if (url.startsWith("http") || url.startsWith("https")){
      view?.loadUrl(url)
    }else{
      try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        view?.context?.startActivity(intent)
      }catch (e:Exception){
        e.printStackTrace()
      }
    }
    return true
  }

  /**
   * 重新编写获取自定义文件的方式
   */
  override fun shouldInterceptRequest(view: WebView?,request: WebResourceRequest?): WebResourceResponse? {
    val customResponse = customInterceptRequest(view,request)
    return customResponse ?: super.shouldInterceptRequest(view, request)
  }

  /**
   * 自定义处理网络资源
   */
  open fun customInterceptRequest(view: WebView?,request: WebResourceRequest?): WebResourceResponse?{
    val requestUrl :String = if (request?.url == null) "" else request.url.toString()
    if (requestUrl.startsWith(WebConstants.IMAGE_SCHEMT)){
      val tempUrl = requestUrl.replace(WebConstants.IMAGE_SCHEMT,"")
      val fileIdIndex = tempUrl.indexOf("/")
      if (fileIdIndex > 0){
        val fileId = tempUrl.substring(0,fileIdIndex)
        val imagePath = getImagePath(view?.context,fileId)
        if (File(imagePath).exists()){
          val fileInputStream = FileInputStream(File(imagePath))
          return WebResourceResponse("image/*", "UTF-8", fileInputStream)
        }
      }
    }else if (requestUrl.startsWith(WebConstants.VIDEO_SCHEMT)){
      val tempUrl = requestUrl.replace(WebConstants.VIDEO_SCHEMT,"")
      val fileIdIndex = tempUrl.indexOf("/")
      if (fileIdIndex > 0){
        val fileId = tempUrl.substring(0,fileIdIndex)
        val videoPath = getVideoPath(view?.context,fileId)
        if (File(videoPath).exists()){
          val fileInputStream = FileInputStream(File(videoPath))
          return WebResourceResponse("video/*", "UTF-8", fileInputStream)
        }
      }
    }
    return null
  }


  //根据id获取图片绝对路径
  private fun getImagePath(context: Context?,id: String) :String{
    var filePath = ""
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val selection = MediaStore.Images.Media._ID + "=?"
    val selectionArgs = arrayOf(id)
    val cursor = context?.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,selection,selectionArgs, null)
    cursor?.let {
      val columnIndex = it.getColumnIndex(projection[0])
      if (it.moveToFirst()) {
        filePath = cursor.getString(columnIndex)
      }
    }
    cursor?.close()
    return filePath
  }

  //根据id获取视频绝对路径
  private fun getVideoPath(context: Context?,id: String) :String{
    var filePath = ""
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val selection = MediaStore.Video.Media._ID + "=?"
    val selectionArgs = arrayOf(id)
    val cursor = context?.contentResolver?.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,selection,selectionArgs, null)
    cursor?.let {
      val columnIndex = it.getColumnIndex(projection[0])
      if (it.moveToFirst()) {
        filePath = cursor.getString(columnIndex)
      }
    }
    cursor?.close()
    return filePath
  }

}