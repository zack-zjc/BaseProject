package com.base.sdk.web

import com.base.sdk.web.config.WebCustomSetting
import com.base.sdk.web.config.WebSetting

/**
 * author:zack
 * Date:2019/3/28
 * Description:web浏览器的设置
 */
object WebLib {

  /**
   * whiteList:网页支持打开的白名单网页
   * userAgent：网页的UA
   * jsInterfaceName:网页代理的名称
   * toolBarColor：网页界面的toolbar颜色
   * isDarkToolbar:toolbar颜色是否是深色的
   */
  fun init(webSetting: WebSetting){
    WebCustomSetting.webSetting = webSetting
  }

}