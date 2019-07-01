package com.base.sdk.web.ui

import android.app.Activity
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.alibaba.fastjson.JSONObject
import com.base.sdk.web.config.WebCustomSetting
import com.base.sdk.web.constant.WebConstants
import com.base.sdk.web.jsinterface.JScriptInterface
import java.io.File
import java.util.*


open class ActWebView : ActBaseWebView() {

  private val REQUEST_CAPTURE_IMAGE = 88 //拍照
  private val REQUEST_PICK_IMAGE = 89 //相册
  private val REQUEST_PICK_VIDEO = 90 //视频
  private val REQUEST_CROP_IMAGE = 91 //裁剪

  //裁剪的x比例
  private var ratioX :Int = 0

  //裁剪的y比例
  private var ratioY :Int = 0

  //js接口
  private var jsInterface : JScriptInterface = JScriptInterface(this)

  //本地路径
  private var tempPicturePath:String = File(Environment.getExternalStorageDirectory(), "webPicture.jpg").absolutePath

  //回调名称setJavaScriptCanOpenWindowsAutomatically
  private lateinit var callbackName:String

  override fun getJsInterface(): JScriptInterface = jsInterface

  override fun statusBarColor(): Int = WebCustomSetting.getToolBarColor()

  /**
   * 挑选图片
   * rationX：X比例0表示不裁剪
   * rationY：Y比例0表示不裁剪
   */
  open fun pickImage(ratioX:Int,ratioY:Int,callbackName:String){
    tempPicturePath = File(Environment.getExternalStorageDirectory(), "webPicture-${System.currentTimeMillis()}.jpg").absolutePath
    this.callbackName = callbackName
    this.ratioX = ratioX
    this.ratioY = ratioY
    requestCustomPermission(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
        title = "温馨提示",desc = "当前操作需要获取您的[存储]权限存储照片!"){ gainPermission,forbidForever ->
      if (gainPermission){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // 设置文件类型
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
      }
    }
  }

  /**
   * 拍照选图片
   * rationX：X比例0表示不裁剪
   * rationY：Y比例0表示不裁剪
   */
  open fun takePhoto(ratioX:Int,ratioY:Int,callbackName:String){
    tempPicturePath = File(Environment.getExternalStorageDirectory(), "webPicture-${System.currentTimeMillis()}.jpg").absolutePath
    this.callbackName = callbackName
    this.ratioX = ratioX
    this.ratioY = ratioY
    requestCustomPermission(arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
        title = "温馨提示",desc = "当前操作需要获取您的[相机]权限用于拍照和[存储]权限存储照片!"){ gainPermission,forbidForever ->
      if (gainPermission){
        captureImage()
      }
    }
  }

  /**
   * 拍照
   */
  private fun captureImage(){
    // 启动系统相机
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val mImageCaptureUri: Uri
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      //临时添加一个拍照权限
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      val contentUri = FileProvider.getUriForFile(this,"$packageName.FileProvider",
          File(tempPicturePath)
      )
      intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
    } else {
      mImageCaptureUri = Uri.fromFile(File(tempPicturePath))
      intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
    }
    startActivityForResult(intent, REQUEST_CAPTURE_IMAGE)
  }

  /**
   * 挑选视频
   */
  open fun pickVideo(callbackName:String){
    this.callbackName = callbackName
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "video/*" // 设置文件类型
    startActivityForResult(intent, REQUEST_PICK_VIDEO)
  }

  /**
   * 裁剪图片方法实现
   * @param uri
   */
  private fun startPhotoZoom(uri: Uri?) {
    val intent = Intent("com.android.camera.action.CROP")
    val bundle = Bundle()
    intent.putExtras(bundle)
    intent.setDataAndType(uri, "image/*")
    intent.putExtra("crop", "true")
    intent.putExtra("circleCrop", "true")
    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", ratioX)
    intent.putExtra("aspectY", ratioY)
    // outputX outputY 是裁剪图片宽高
    intent.putExtra("scale", true)
    intent.putExtra("outputX", ratioX*100)
    intent.putExtra("outputY", ratioY*100)
    intent.putExtra("return-data", true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      //开启临时权限
      intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
      //重点:针对7.0以上的操作
      intent.clipData = ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri)
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(tempPicturePath)))
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
    intent.putExtra("noFaceDetection", true)
    startActivityForResult(intent, REQUEST_CROP_IMAGE)
  }

  //获取文件uri
  private fun getPathUri(filePath:String):Uri{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      FileProvider.getUriForFile(this,"$packageName.FileProvider",File(filePath))
    } else {
       Uri.fromFile(File(tempPicturePath))
    }
  }

  override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK){
      if (requestCode == REQUEST_PICK_IMAGE) { //相册
        data?.let {
          if (ratioX > 0 && ratioY > 0){
            startPhotoZoom(it.data)
          }else{
            callbackImageUriData(it.data)
          }
        }
      } else if (requestCode == REQUEST_CAPTURE_IMAGE) {  //相机
        if (ratioX > 0 && ratioY > 0){
          startPhotoZoom(getPathUri(tempPicturePath))
        }else{
          callbackImageData(tempPicturePath)
        }
      } else if (requestCode == REQUEST_CROP_IMAGE) { //裁剪
        callbackImageData(tempPicturePath)
      } else if (requestCode == REQUEST_PICK_VIDEO){ //视频
        data?.let {
          callbackVideoData(it.data)
        }
      }
    }
  }

  /**
   * 返回图片数据
   */
  private fun callbackImageUriData(uri: Uri?){
    Thread(Runnable {
      val imageId = getImageUriId(uri)
      val filePath = getImageUriAbsolutePath(uri)
      val file = File(filePath)
      val json = JSONObject()
      json["size"] = file.length()
      json["name"] = file.name
      json["path"] = file.absolutePath
      val sign = UUID.randomUUID().toString()
      json["sign"] = sign
      json["fileId"] = "${WebConstants.IMAGE_SCHEMT}$imageId/$sign"
      runOnUiThread {
        getWebView()?.loadUrl("javascript:$callbackName($json);")
      }
    }).start()
  }

  /**
   * 返回图片数据
   */
  private fun callbackImageData(filePath:String){
    Thread(Runnable {
      val imageId = getImagePathId(filePath)
      val file = File(filePath)
      val json = JSONObject()
      json["size"] = file.length()
      json["name"] = file.name
      json["path"] = file.absolutePath
      val sign = UUID.randomUUID().toString()
      json["sign"] = sign
      json["fileId"] = "${WebConstants.IMAGE_SCHEMT}$imageId/$sign"
      runOnUiThread {
        getWebView()?.loadUrl("javascript:$callbackName($json);")
      }
    }).start()
  }

  //获取图片数据库id
  private fun getImageUriId(uri: Uri?):String{
    val indexTemp = uri.toString().lastIndexOf("/")
    return uri.toString().substring(indexTemp+1)
  }

  //根据uri获取图片绝对路径
  private fun getImageUriAbsolutePath(uri: Uri?) :String{
    var filePath = ""
    val id = getImageUriId(uri)
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val selection = MediaStore.Images.Media._ID + "=?"
    val selectionArgs = arrayOf(id)
    val cursor = this.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,selection
        ,selectionArgs, null)
    cursor?.let {
      val columnIndex = it.getColumnIndex(projection[0])
      if (it.moveToFirst()) {
        filePath = cursor.getString(columnIndex)
      }
    }
    cursor?.close()
    return filePath
  }

  //获取图片id
  private fun getImagePathId(imagePath:String):String{
    val contentValues = ContentValues(1)
    contentValues.put(MediaStore.Images.Media.DATA, imagePath)
    this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    var fileId = ""
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = MediaStore.Images.Media.DATA + "=?"
    val selectionArgs = arrayOf(imagePath)
    val cursor = this.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,selection
        ,selectionArgs, null)
    cursor?.let {
      val columnIndex = it.getColumnIndex(projection[0])
      if (it.moveToFirst()) {
        fileId = cursor.getString(columnIndex)
      }
    }
    cursor?.close()
    return fileId
  }

  /**
   * 返回视频数据
   */
  private fun callbackVideoData(uri: Uri?){
    Thread(Runnable {
      val videoPath = getVideoUriAbsolutePath(uri)
      val videoId = getVideoId(uri)
      val file = File(videoPath)
      val json = JSONObject()
      json["size"] = file.length()
      json["name"] = file.name
      json["path"] = file.absolutePath
      val sign = UUID.randomUUID().toString()
      json["sign"] = sign
      json["fileId"] = "${WebConstants.VIDEO_SCHEMT}$videoId/$sign"
      runOnUiThread {
        getWebView()?.loadUrl("javascript:$callbackName($json);")
      }
    }).start()
  }

  //根据uri获取视频绝对路径
  private fun getVideoUriAbsolutePath(uri: Uri?) :String{
    var filePath = ""
    val id = getVideoId(uri)
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val selection = MediaStore.Video.Media._ID + "=?"
    val selectionArgs = arrayOf(id)
    val cursor = this.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,selection
        ,selectionArgs, null)
    cursor?.let {
      val columnIndex = it.getColumnIndex(projection[0])
      if (it.moveToFirst()) {
        filePath = cursor.getString(columnIndex)
      }
    }
    cursor?.close()
    return filePath
  }

  //获取视频id
  private fun getVideoId(uri: Uri?):String{
    val indexTemp = uri.toString().lastIndexOf("/")
    return uri.toString().substring(indexTemp+1)
  }

}
