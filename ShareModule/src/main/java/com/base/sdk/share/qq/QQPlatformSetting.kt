package com.base.sdk.share.qq

import com.base.sdk.share.platform.PlatformConfig
import com.base.sdk.share.platform.PlatformSetting

/**
 * author:zack
 * Date:2019/3/26
 * Description:qq分享设置
 */
abstract class QQPlatformSetting(private val appId: String) : PlatformSetting{

  override fun getPlatformConfig(): PlatformConfig {
    return PlatformConfig(appId = appId)
  }
}