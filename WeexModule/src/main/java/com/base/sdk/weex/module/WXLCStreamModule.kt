package com.base.sdk.weex.module

import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.fetch.HttpClientUtil
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule

/**
 * author:zack
 * Date:2019/3/27
 * Description:网络请求的module
 */
open class WXLCStreamModule : WXModule() {

  /**
   *参数传递参照网络请求传递参数
   * 网络请求使用HTTPCLIENT
   */
  @JSMethod(uiThread = false)
  open fun fetch(options: Map<String, Any>,callback: JSCallback) {
    val jsonObject = JSONObject()
    for ((key,vaule) in options){
      jsonObject[key] = vaule
    }
    val result = HttpClientUtil.fetch(jsonObject)
    callback.invoke(result)
  }

}