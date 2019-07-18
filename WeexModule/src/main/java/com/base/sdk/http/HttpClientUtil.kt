package com.base.sdk.http

import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.util.StreamUtil

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
    val server = options["server"].toString()
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
    return StreamUtil.stream(method, path, server, queryParam, body, header, followRedirect)
  }

}