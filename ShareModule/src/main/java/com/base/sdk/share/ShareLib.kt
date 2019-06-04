package com.base.sdk.share

import android.app.Application
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.config.ShareConfig
import com.base.sdk.share.platform.PlatformSetting

/**
 * author:zack
 * Date:2019/3/26
 * Description:分享设置
 */
object ShareLib {

  /**
   * 初始化application
   */
  fun init(application: Application){
    ApplicationContext.CONTEXT = application
  }

  /**
   * 初始化对应的存在的分享平台
   */
  fun initPlatform(configs :List<PlatformSetting>){
    ShareConfig.sharePlatformList.clear()
    ShareConfig.sharePlatformList.addAll(configs)
    for (config in configs){
      config.getSharePlatform().configPlatform(config.getPlatformConfig())
    }
  }

}