package com.base.sdk.share.platform

import android.app.Activity
import android.content.Intent
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformConfig

/**
 * author:zack
 * Date:2019/3/26
 * Description:分享接口
 */
interface SharePlatformInterface {

  /**
   * 配置平台信息
   */
  fun configPlatform(config: PlatformConfig)

  /**
   * 当前平台是否安装
   */
  fun isPlatformInstalled():Boolean

  /**
   * 注册应用
   */
  fun registerApp(context: Activity)

  /**
   * 分享数据
   */
  fun share(shareEntity: ShareEntity,callback: Callback?)

  /**
   * 处理返回数据
   */
  fun handleIntent(requestCode: Int,resultCode: Int,data : Intent?)

}