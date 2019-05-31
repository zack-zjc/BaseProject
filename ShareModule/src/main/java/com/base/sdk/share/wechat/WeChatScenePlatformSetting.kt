package com.base.sdk.share.wechat

import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.R
import com.base.sdk.share.platform.SharePlatformInterface

/**
 * author:zack
 * Date:2019/3/26
 * Description:朋友圈
 */
open class WeChatScenePlatformSetting(appId: String) : WeChatPlatformSetting(appId){

  override fun getPlatformLogo(): Int {
    return R.mipmap.icon_share_wechat_scene
  }

  override fun getPlatformName(): String {
    return ApplicationContext.CONTEXT.resources.getString(R.string.str_share_wechat_scence)
  }

  override fun getSharePlatform(): SharePlatformInterface {
    return WeChatSceneUtil.instance
  }

}