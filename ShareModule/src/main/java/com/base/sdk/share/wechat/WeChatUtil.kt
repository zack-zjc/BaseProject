package com.base.sdk.share.wechat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformConfig
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream

/**
 * author:zack
 * Date:2019/3/1
 * Description:微信通用util
 */
open class WeChatUtil :IWXAPIEventHandler{

  //是第三方app和微信通信的openApi接口
  private var mApi :IWXAPI? = null

  companion object {
    val instance by lazy { WeChatUtil() }
  }

  /**
   * 获取mApi
   */
  fun getIWXAPI():IWXAPI? = mApi

  /**
   * 配置wechat
   */
  fun configWechat(config: PlatformConfig){
    if (mApi == null){
      mApi = WXAPIFactory.createWXAPI(ApplicationContext.CONTEXT,config.appId, true)
      val result = mApi?.registerApp(config.appId)
      if (result != true){
        mApi = null
      }
    }
  }

  /**
   * 微信是否安装
   */
  fun isPlatformInstalled() = mApi?.isWXAppInstalled?:false

  /**
   * 将应用注册到微信
   */
  fun registerApp(context: Activity){

  }

  /**
   * 处理返回
   */
  fun handleIntent(data: Intent?) {
    mApi?.handleIntent(data,this)
  }

  /**
   * 分享
   * WXImageObject （WXMediaMessage.IMediaObject的派生类，用于描述一个图片对象）
   *
   */
  fun share(shareEntity: ShareEntity,shareType:Int) {
    val title = if (shareEntity.getShareTitleText().isEmpty()) shareEntity.getShareDescriptionText() else shareEntity.getShareTitleText()
    val message = if (shareEntity.getShareDescriptionText().isEmpty()) shareEntity.getShareTitleText() else shareEntity.getShareDescriptionText()
    val request = SendMessageToWX.Req()
    request.transaction = System.currentTimeMillis().toString()
    request.scene = shareType //获取分享的类型是朋友圈还是好友
    lateinit var mediaMessage :WXMediaMessage
    if (!shareEntity.isShareHtmlEmpty()){
      //webpageUrl	String	html链接	限制长度不超过10KB
      mediaMessage = WXMediaMessage(WXWebpageObject(shareEntity.getShareHtml()))
      if (!shareEntity.isShareImageEmpty()){
        mediaMessage.thumbData = bitmapToByteArray(shareEntity.getShareImageBitmap())
      }
    } else if (!shareEntity.isShareImageEmpty()){
      //图片的本地路径,对应图片内容大小不超过10MB
      mediaMessage = WXMediaMessage(WXImageObject(shareEntity.getShareImageBitmap()))
    } else {
      //text	String	文本数据	长度需大于0且不超过10KB
      mediaMessage = WXMediaMessage(WXTextObject(
          if (!shareEntity.isShareTitleEmpty()) shareEntity.getShareTitleText() else shareEntity.getShareDescriptionText()))
    }
    mediaMessage.title = title
    mediaMessage.description = message
    request.message = mediaMessage
    mApi?.sendReq(request)
  }

  /**
   * 转化bitmap为byte数组
   */
  private fun bitmapToByteArray(bmp: Bitmap): ByteArray? {
    var result :ByteArray? = null
    try {
      val bitmap = Bitmap.createScaledBitmap(bmp, 72, 72, true)
      val output = ByteArrayOutputStream()
      bitmap.compress(CompressFormat.PNG, 100, output)
      result = output.toByteArray()
      bitmap.recycle()
      output.close()
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return result
  }

  /**
   * 微信回调
   * 分享无回调
   */
  override fun onResp(response: BaseResp?) {

  }

  /**
   * 微信回调
   * 分享无回调
   */
  override fun onReq(request: BaseReq?) {

  }


}