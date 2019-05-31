package com.base.sdk.share.weibo

import android.app.Activity
import android.content.Intent
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformConfig
import com.base.sdk.share.platform.SharePlatformInterface
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.share.WbShareCallback
import com.sina.weibo.sdk.share.WbShareHandler

/**
 * author:zack
 * Date:2019/3/4
 * Description:微博分享
 */
class WeiboUtil : WbShareCallback, SharePlatformInterface {

  private var inited = false

  private var shareHandler: WbShareHandler? = null

  private var callback:Callback? = null

  companion object {
    val instance by lazy { WeiboUtil() }
  }

  /**
   * 配置平台
   */
  override fun configPlatform(config: PlatformConfig) {
    if (!inited){
      inited = true
      //微博初始化注意包名和应用签名要一致以及对应一下参数
      WbSdk.install(ApplicationContext.CONTEXT, AuthInfo(ApplicationContext.CONTEXT,config.appKey,config.appRedirect,config.appInfo))
    }
  }

  /**
   * 微博平台是否安装
   */
  override fun isPlatformInstalled() :Boolean = WbSdk.isWbInstall(
      ApplicationContext.CONTEXT)

  /**
   * 注册app到微博
   */
  override fun registerApp(context: Activity){
    shareHandler = WbShareHandler(context)
    shareHandler?.registerApp()
  }

  /**
   * 分享
   */
  override fun share(shareEntity: ShareEntity,callback: Callback?){
    this.callback = callback
    val weiboMessage = WeiboMultiMessage()
    val title = if (shareEntity.getShareTitleText().isEmpty()) shareEntity.getShareDescriptionText() else shareEntity.getShareTitleText()
    val message = if (shareEntity.getShareDescriptionText().isEmpty()) shareEntity.getShareTitleText() else shareEntity.getShareDescriptionText()
    if (!shareEntity.isShareHtmlEmpty()){
      val pageObject = WebpageObject()
      pageObject.title = title
      pageObject.description = message
      pageObject.actionUrl = shareEntity.getShareHtml()
      if (!shareEntity.isShareImageEmpty()){
        pageObject.setThumbImage(shareEntity.getShareImageBitmap())
      }
      weiboMessage.mediaObject = pageObject
    }else {
      if (!shareEntity.isShareImageEmpty()){
        //不支持多图分享的客户端默认使用单图分享
        val imageObject = ImageObject()
        imageObject.setImageObject(shareEntity.getShareImageBitmap())
        weiboMessage.imageObject = imageObject
      }
      val textObject = TextObject()
      textObject.text = message
      weiboMessage.textObject = textObject
    }
    shareHandler?.shareMessage(weiboMessage, false)
  }

  /**
   * 处理onActivityResult
   */
  override fun handleIntent(requestCode: Int,resultCode: Int,data : Intent?){
    shareHandler?.doResultIntent(data,this)
  }

  /**
   * 分享失败
   */
  override fun onWbShareFail() {
    callback?.onFail(this)
  }

  /**
   * 分享取消
   */
  override fun onWbShareCancel() {
    callback?.onUserCancel(this)
  }

  /**
   * 分享成功
   */
  override fun onWbShareSuccess() {
    callback?.onSuccess(this)
  }

}