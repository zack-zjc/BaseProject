package com.base.sdk.share.weibo

import com.base.sdk.share.R
import com.base.sdk.share.platform.SharePlatformInterface
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.platform.PlatformConfig
import com.base.sdk.share.platform.PlatformSetting

/**
 * author:zack
 * Date:2019/3/26
 * Description:微博分享的设置
 */
open class WeiBoPlatformSetting(private val appKey:String,private val appRedirect:String,private val scope:String) : PlatformSetting{

  override fun getPlatformLogo(): Int {
    return R.mipmap.icon_share_sina
  }

  override fun getPlatformName(): String {
    return ApplicationContext.CONTEXT.resources.getString(R.string.str_share_sina)
  }

  override fun getPlatformConfig(): PlatformConfig {
    return PlatformConfig(appKey=appKey,appRedirect = appRedirect,appInfo = scope)
  }

  override fun getSharePlatform(): SharePlatformInterface {
    return WeiboUtil.instance
  }

}