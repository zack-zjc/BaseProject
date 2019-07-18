package com.base.sdk.weex

import android.app.Application
import com.base.sdk.weex.adapter.WXLCDrawableAdapter
import com.base.sdk.weex.adapter.WXLCImageAdapter
import com.base.sdk.weex.adapter.WXLCJSExceptionAdapter
import com.base.sdk.weex.module.WXLCPageModule
import com.base.sdk.weex.module.WXLCStreamModule
import com.taobao.weex.InitConfig
import com.taobao.weex.WXSDKEngine

/**
 * author:zack
 * Date:2019/3/27
 * Description:weexLib入口
 */
object WeexLib {

  /**
   * 初始化WEEX环境
   * application:context
   * weexInterface:对应注册的模块
   */
  fun init(application: Application){
    //注册加载模块
    val configBuilder = InitConfig.Builder()
    configBuilder.setImgAdapter(WXLCImageAdapter())
    configBuilder.setDrawableLoader(WXLCDrawableAdapter(application))
    configBuilder.setJSExceptionAdapter(WXLCJSExceptionAdapter())
    WXSDKEngine.initialize(application,configBuilder.build())

    //注册模块
    WXSDKEngine.registerModule("wx_stream", WXLCStreamModule::class.java)
    WXSDKEngine.registerModule("wx_page", WXLCPageModule::class.java)

  }


}