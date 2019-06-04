package com.base.sdk.http

import android.app.Application
import com.base.sdk.http.config.HttpConfig
import com.base.sdk.http.config.HttpSetting

/**
 * author:zack
 * Date:2019/3/25
 * Description:网络请求库的初始化类
 */
object HttpClientLib {

  /**
   * 初始化httpclient
   * whiteHost:白名单列表
   * defaultHost：默认请求host
   * userAgent：请求userAgent
   * useCookie:是否使用cookie
   */
  fun init(application: Application,setting:HttpSetting){
    ApplicationContext.CONTEXT = application
    HttpConfig.init(setting)
  }

  /**
   * 设置当前模式
   * debug：是否是debug的模式
   */
  fun setMode(debug:Boolean){
    HttpConfig.isDebug = debug
  }

}