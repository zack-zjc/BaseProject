package com.base.sdk.share.callback

import com.base.sdk.share.platform.SharePlatformInterface

/**
 * author:zack
 * Date:2019/4/9
 * Description:分享结果处理
 */
interface Callback {

  /**
   * 分享成功
   */
  fun onSuccess(platformInterface: SharePlatformInterface)

  /**
   * 分享失败
   */
  fun onFail(platformInterface: SharePlatformInterface)

  /**
   * 分享取消
   */
  fun onUserCancel(platformInterface: SharePlatformInterface)

}