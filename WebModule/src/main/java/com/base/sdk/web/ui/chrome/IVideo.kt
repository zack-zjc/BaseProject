package com.base.sdk.web.ui.chrome

import android.view.View
import android.webkit.WebChromeClient



/**
 * author:zack
 * Date:2019/4/17
 * Description:网页视频全屏的interface
 */
interface IVideo {

  /**
   * 展示视频
   */
  fun onShowCustomView(view: View,callback: WebChromeClient.CustomViewCallback)

  /**
   * 隐藏视频
   */
  fun onHideCustomView()

  /**
   * 当前是否是视频全屏状态
   */
  fun isVideoState(): Boolean

}