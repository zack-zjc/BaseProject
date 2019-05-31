package com.base.sdk.weex

import android.app.Application
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
  fun init(application: Application,configBuilder:InitConfig.Builder,weexInterface:WeexInterface=DefaultWeexInterface()){
    //注册加载模块
    configBuilder.setImgAdapter(weexInterface.getImageAdapter())
    configBuilder.setDrawableLoader(weexInterface.getDrawableAdapter(application))
    configBuilder.setJSExceptionAdapter(weexInterface.getExceptionAdapter())
    configBuilder.setHttpAdapter(weexInterface.getHttpAdapter())
    WXSDKEngine.initialize(application,configBuilder.build())

    //注册模块
    WXSDKEngine.registerModule(weexInterface.getWxStreamModuleName(), weexInterface.getWxStreamModule())
    WXSDKEngine.registerModule(weexInterface.getWxEventModuleName(), weexInterface.getWxEventModule())
    WXSDKEngine.registerModule(weexInterface.getWxPageModuleName(), weexInterface.getWxPageModule())

  }


}