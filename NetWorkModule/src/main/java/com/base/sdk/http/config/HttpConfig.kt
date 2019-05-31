package com.base.sdk.http.config

import com.base.sdk.http.decode.ResponseDecoder
import com.base.sdk.http.encoder.ContentEncoder

/**
 * author:zack
 * Date:2019/3/25
 * Description:网络的相关配置
 */
object HttpConfig {

  private lateinit var httpSetting :HttpSetting

  /**
   * 当前是否是debug模式
   * debug状态会展示一些请求
   */
  var isDebug = false

  /**
   * 设置默认的网络请求的host
   * 添加网络请求白名单地址
   * 设置当前ua
   */
  fun init(setting :HttpSetting){
    this.httpSetting = setting
  }

  /**
   * 获取UA
   */
  fun getUserAgent() = httpSetting.userAgent

  /**
   * 获取请求后缀
   */
  fun getRequestUrlSuffix() = httpSetting.requestUrlSuffix

  /**
   * 获取okhttp的设置
   */
  fun getClientSetting() = httpSetting.setting

  /**
   * 返回默认的域名
   */
  fun getDefaultHost() = httpSetting.defaultHost

  /**
   * 返回错误处理器
   */
  fun getErrorHandler() = httpSetting.handler

  /**
   * 当前host是否在白名单中
   */
  fun isInWhiteList(host:String):Boolean = httpSetting.whiteHost.contains(host)

  /**
   * 获取对应可以处理当前type的encoder
   */
  fun getContentEncoder(contentType:String) :ContentEncoder?{
    for (encoder in httpSetting.contentEncoderList){
      if (encoder.canProcessContent(contentType)){
        return encoder
      }
    }
    return null
  }

  /**
   * 获取对应可以处理当前accept的decoder
   */
  fun getAcceptDecoder(accept:String) :ResponseDecoder<out Any>?{
    for (decoder in httpSetting.acceptDecoderList){
      if (decoder.canProcessResponse(accept)){
        return decoder
      }
    }
    return null
  }

}