package com.base.sdk.http.config

import com.base.sdk.http.decode.ResponseDecoder
import com.base.sdk.http.decode.impl.StringResponseDecoder
import com.base.sdk.http.encoder.ContentEncoder
import com.base.sdk.http.encoder.impl.FormContentEncoder
import com.base.sdk.http.encoder.impl.JsonContentEncoder
import com.base.sdk.http.encoder.impl.MultiPartContentEncoder
import com.base.sdk.http.handler.ErrorHandler
import com.base.sdk.http.handler.impl.DefaultErrorHandler
import com.base.sdk.http.httpclient.ClientSettingInterface
import com.base.sdk.http.httpclient.DefaultClientSetting

/**
 * author:zack
 * Date:2019/3/29
 * Description:http的设置
 */
class HttpSetting {

  //默认的host
  lateinit var defaultHost :String

  //请求UA
  var  userAgent:String? = null

  //请求后缀
  var requestUrlSuffix:String? = null

  //允许的报名单地址
  lateinit var  whiteHost:List<String>

  //okHttpClient的设置
  lateinit var  setting: ClientSettingInterface

  //body解析器
  lateinit var  contentEncoderList:List<ContentEncoder>

  //返回response解析器
  lateinit var  acceptDecoderList :List<ResponseDecoder<out Any>>

  //错误处理器
  lateinit var  handler: ErrorHandler

  class Builder(defaultHostStr:String) {

    private var defaultHost :String = defaultHostStr

    private var  whiteHost:ArrayList<String> = arrayListOf()

    private var  userAgent:String? = null

    private var requestUrlSuffix:String? = null

    private var  setting: ClientSettingInterface = DefaultClientSetting()

    private var  encoders:ArrayList<ContentEncoder> = arrayListOf(JsonContentEncoder(), FormContentEncoder(), MultiPartContentEncoder())

    private var  decoders :ArrayList<ResponseDecoder<out Any>> = arrayListOf(StringResponseDecoder())

    private var  handler: ErrorHandler = DefaultErrorHandler()

    /**
     * 添加白名单
     */
    fun addWhiteHost(hosts:List<String>):Builder{
      this.whiteHost.addAll(hosts)
      return this
    }

    /**
     * 替换白名单
     */
    fun replaceWhiteHost(hosts:List<String>):Builder{
      this.whiteHost.clear()
      this.whiteHost.addAll(hosts)
      return this
    }

    /**
     * 设置UA
     */
    fun userAgent(userAgent:String):Builder{
      this.userAgent = userAgent
      return this
    }

    /**
     * 设置请求后缀
     */
    fun addRequestUrlSuffix(urlSuffix:String):Builder{
      this.requestUrlSuffix = urlSuffix
      return this
    }

    /**
     * OKHTTP的设置
     */
    fun clientSetting(setting:ClientSettingInterface):Builder{
      this.setting = setting
      return this
    }

    /**
     * 设置encoder
     */
    fun addContentEncoder(encoders:List<ContentEncoder>):Builder{
      this.encoders.addAll(encoders)
      return this
    }

    /**
     * 替换encoder
     */
    fun replaceContentEncoder(encoders:List<ContentEncoder>):Builder{
      this.encoders.clear()
      this.encoders.addAll(encoders)
      return this
    }

    /**
     * 设置decoder
     */
    fun addResponseDecoder(decoders:List<ResponseDecoder<out Any>>):Builder{
      this.decoders.addAll(decoders)
      return this
    }

    /**
     * 替换decoder
     */
    fun replaceResponseDecoder(decoders:List<ResponseDecoder<out Any>>):Builder{
      this.decoders.clear()
      this.decoders.addAll(decoders)
      return this
    }

    /**
     * 错误处理器
     */
    fun errorHandler(handler: ErrorHandler):Builder{
      this.handler = handler
      return this
    }

    /**
     * 创建对象
     */
    fun build():HttpSetting{
      val httpSetting = HttpSetting()
      httpSetting.defaultHost = defaultHost
      httpSetting.whiteHost = whiteHost
      httpSetting.userAgent = userAgent
      httpSetting.requestUrlSuffix = requestUrlSuffix
      httpSetting.setting = setting
      httpSetting.acceptDecoderList = decoders
      httpSetting.contentEncoderList = encoders
      httpSetting.handler = handler
      return httpSetting
    }

  }

}