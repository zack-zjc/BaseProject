package com.base.sdk.http.util

import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.decoder.DecoderUtil
import com.base.sdk.http.encoder.EncoderUtil
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.internal.Util
import java.net.URLEncoder

/**
 * author:zack
 * Date:2019/3/25
 * Description:请求处理类
 */
object StreamUtil {

  /**
   * method:方法
   * path：请求相对路径
   * queryParam：请求query数据，未进行UrlEncoder
   * body：传递的数据
   * header:请求头部
   * followRedirect：是否redirect请求
   */
  fun stream(method:String,path:String,host:String,queryParam: JSONObject,body:Any?,
    header: JSONObject,followRedirect:Boolean):JSONObject{
    val result = JSONObject()
    try {
      var url = host + path
      var queryParamStr = ""
      for ((key,value) in queryParam){
        queryParamStr += "$key=${URLEncoder.encode(value.toString(), "utf-8")}&"
      }
      if (queryParamStr.isNotEmpty()){
        queryParamStr = queryParamStr.substring(0,queryParamStr.length-1)
        url += if (url.contains("?") && url.endsWith("?")){
          queryParamStr
        }else if (url.contains("?")){
          "&$queryParamStr"
        }else{
          "?$queryParamStr"
        }
      }
      val builder = Request.Builder().url(url)
      var contentType = HeaderType.APPLICATION_JSON
      var accept = HeaderType.APPLICATION_JSON
      if (!header.isEmpty()){
        if (header.containsKey(HeaderType.CONTENT_TYPE)) {
          contentType = header[HeaderType.CONTENT_TYPE].toString()
        }
        if (header.containsKey(HeaderType.ACCEPT)) {
          accept = header[HeaderType.ACCEPT].toString()
        }
        for ((key, value) in header) {
          builder.addHeader(key, value.toString())
        }
      }
      if (method.toLowerCase() != "get") {
        lateinit var requestBody: RequestBody
        val encoder = EncoderUtil.getEncoder(contentType)
        encoder?.let {
          requestBody = it.encoder(body?:"")
        }
        when {
          method.toLowerCase() == "post"   ->  builder.post(requestBody)
          method.toLowerCase() == "delete" ->  builder.delete(requestBody)
          method.toLowerCase() == "put"    ->  builder.put(requestBody)
        }
      }
      val request = builder.build()
      val okHttpClient = if (followRedirect) OkHttpUtil.REQUEST_REDIRECT_CLIENT.getOkHttpClient() else OkHttpUtil.REQUEST_CLIENT.getOkHttpClient()
      val response = okHttpClient.newCall(request).execute()
      result["code"] = response.code()
      val headers = response.headers().toMultimap()
      val headerJson = JSONObject()
      for ((key,value) in headers){
        headerJson[key] = value
      }
      result["headers"] = headerJson
      val decoder = DecoderUtil.getDecoder(accept)
      var returnData :Any? = null
      decoder?.let {
        returnData = it.handleResponse(response)
      }
      result["data"] = returnData
      Util.closeQuietly(response)
    }catch (e:Exception){
        result["code"] = -1
    }
    return result
  }

}