package com.base.sdk.share.wechat

import android.app.Activity
import android.content.Intent
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformConfig
import com.base.sdk.share.platform.SharePlatformInterface
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX

/**
 * author:zack
 * Date:2019/3/4
 * Description:朋友圈分享
 */
class WeChatSceneUtil : SharePlatformInterface {

  companion object {
    val instance by lazy { WeChatSceneUtil() }
    var shareCallback :Callback? = null
  }

  override fun configPlatform(config: PlatformConfig) {
    WeChatUtil.instance.configWechat(config)
  }

  override fun isPlatformInstalled() :Boolean = WeChatUtil.instance.isPlatformInstalled()

  override fun registerApp(context: Activity) {
    WeChatUtil.instance.registerApp(context)
  }

  override fun share(shareEntity: ShareEntity,callback: Callback?) {
    WeChatUtil.instance.share(shareEntity,SendMessageToWX.Req.WXSceneTimeline)
    shareCallback = callback
  }

  override fun handleIntent(requestCode: Int,resultCode: Int,data: Intent?) {
    WeChatUtil.instance.handleIntent(data)
  }
}