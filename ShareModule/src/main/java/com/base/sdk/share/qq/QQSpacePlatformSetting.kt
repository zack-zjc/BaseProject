package com.base.sdk.share.qq

import com.base.sdk.share.R
import com.base.sdk.share.platform.SharePlatformInterface
import com.base.sdk.share.config.ApplicationContext

/**
 * author:zack
 * Date:2019/3/26
 * Description:qq空间分享
 */
open class QQSpacePlatformSetting(appId: String) : QQPlatformSetting(appId) {

  override fun getPlatformLogo(): Int {
    return R.mipmap.icon_share_qzone
  }

  override fun getPlatformName(): String {
    return ApplicationContext.CONTEXT.resources.getString(R.string.str_share_qzone)
  }

  override fun getSharePlatform(): SharePlatformInterface {
    return TencentSpaceUtil.instance
  }

}