package com.base.sdk.web.config

import android.net.Uri

/**
 * author:zack
 * Date:2019/3/28
 * Description:对浏览器的自定义设置
 */
object WebCustomSetting {

  lateinit var webSetting:WebSetting

  /**
   * 是否在白名单中
   */
  fun isInWhiteList(url:String):Boolean{
    var result = false
    val urlDomain = Uri.parse(url).host?:""
    for (whiteHost in webSetting.whiteHost){
      if (urlDomain == whiteHost){
        result = true
        break
      }
    }
    return result
  }

  /**
   * 是否检测网址白名单
   */
  fun checkWhiteHost():Boolean = !webSetting.whiteHost.isEmpty()

  /**
   * 获取js的名称
   */
  fun getJsInterfaceName() = webSetting.jsInterfaceName

  /**
   * 获取浏览器的userAgent
   */
  fun getUserAgent() = webSetting.userAgent

  /**
   * 获取自定义的toobar颜色
   * 设置导航栏的颜色
   */
  fun getToolBarColor() = webSetting.toolBarColor

  /**
   * 是否使用的深色的toolbar颜色
   * 判断使用白色的导航栏图标和文字颜色
   */
  fun isUseDarkToolbar() = webSetting.isDarkToolbar

}