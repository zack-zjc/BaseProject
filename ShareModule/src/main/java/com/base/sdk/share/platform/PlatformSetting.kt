package com.base.sdk.share.platform

/**
 * author:zack
 * Date:2019/3/26
 * Description:平台设置数据
 * platformLogo:平台对应的logo
 * platformName：平台对应的名称
 * platformConfig：平台对应的配置
 */
interface PlatformSetting {

  /**
   * 展示dialog的logo
   */
  fun getPlatformLogo():Int

  /**
   * 展示dialog的名字
   */
  fun getPlatformName():String

  /**
   * 平台所需的配置
   */
  fun getPlatformConfig():PlatformConfig

  /**
   * 平台分享接口
   */
  fun getSharePlatform(): SharePlatformInterface

}