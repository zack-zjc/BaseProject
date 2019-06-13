package com.base.sdk.http.fetch

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.config.HttpConfig

/**
 * author:zack
 * Date:2019/3/25
 * Description:网络请求封装库
 */
object HttpClientUtil {

  /**
   * 通用的网络请求
   * method:方法
   * path：请求相对路径
   * queryParam：请求query数据，未进行UrlEncoder
   * body：传递的数据
   * header:请求头部
   * followRedirect：是否redirect请求
   */
  fun fetch(options:JSONObject): JSONObject {
    val method = options["method"].toString()
    val path = options["path"].toString()
    var server = HttpConfig.getDefaultHost()
    if (options.containsKey("server")){
      server = options["server"].toString()
    }
    var queryParam = JSONObject()
    if (options.containsKey("query")){
      queryParam = options["query"] as JSONObject
    }
    var body :Any? = null
    if (options.containsKey("body")){
      body = options["body"]
    }
    var header = JSONObject()
    if (options.containsKey("headers")){
      header = options["headers"] as JSONObject
    }
    var followRedirect = true
    if (options.containsKey("followRedirect")){
      followRedirect = options["followRedirect"] as Boolean
    }
    val result = StreamUtil.stream(method, path, server, queryParam, body, header, followRedirect)
    HttpConfig.getErrorHandler().handleError(result["code"].toString().toInt(),result["data"])
    return result
  }

  /**
   * 通用的网络请求
   * method:方法
   * path：请求相对路径
   * queryParam：请求query数据，未进行UrlEncoder
   * body：传递的数据
   * header:请求头部
   * followRedirect：是否redirect请求
   * T:返回数据类型，需要序列化
   */
  fun <T> fetch(options:JSONObject,responseType:Class<T>) : JSONObject{
    val result = fetch(options)
    result["data"] = JSON.parseObject(result["data"].toString(),responseType)
    return result
  }



}