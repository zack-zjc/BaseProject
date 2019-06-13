package com.base.sdk.http.fetch

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.ApplicationContext
import com.base.sdk.http.HttpConstants
import com.base.sdk.http.config.HttpConfig
import com.base.sdk.http.content.Accept
import com.base.sdk.http.content.ContentType
import com.base.sdk.http.httpclient.OkHttpUtil
import com.base.sdk.http.httpclient.OkHttpUtil.REQUEST_CLIENT
import okhttp3.Request
import okhttp3.RequestBody
import org.apache.http.conn.ConnectTimeoutException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

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
    val urlDomain = Uri.parse(host).host?:""
    if (!HttpConfig.isInWhiteList(urlDomain)){
      result["code"] = HttpConstants.HTTP_ERROR_WHITE_LIST_EXCEPTION
      return result
    }
    if (!isNetworkOn()){
      result["code"] = HttpConstants.HTTP_ERROR_NETWORK
      return result
    }
    try {
      var url = host + path
      val urlRequestSuffix = HttpConfig.getRequestUrlSuffix()
      if (!urlRequestSuffix.isNullOrEmpty()){
        if (url.contains("?") && url.endsWith("?")){
          url += urlRequestSuffix
        }else if (url.contains("?")){
          url += "&$urlRequestSuffix"
        }else{
          url += "?$urlRequestSuffix"
        }
      }
      var queryParamStr = ""
      for ((key,value) in queryParam){
        queryParamStr += "$key=${URLEncoder.encode(value.toString(), "utf-8")}"
      }
      if (queryParamStr.isNotEmpty()){
        if (url.contains("?") && url.endsWith("?")){
          url += queryParamStr
        }else if (url.contains("?")){
          url += "&$queryParamStr"
        }else{
          url += "?$queryParamStr"
        }
      }
      val builder = Request.Builder().url(url)
      var contentType = ContentType.APPLICATION_JSON
      var accept = Accept.APPLICATION_JSON
      if (!header.isEmpty()){
        if (header.containsKey(ContentType.CONTENT_TYPE)) {
          contentType = header[ContentType.CONTENT_TYPE].toString()
        }
        if (header.containsKey(Accept.ACCEPT)) {
          accept = header[Accept.ACCEPT].toString()
        }
        for ((key, value) in header) {
          builder.addHeader(key, value.toString())
        }
        HttpConfig.getUserAgent()?.let {
          builder.addHeader("User-Agent",it)
        }
      }
      if (method.toLowerCase() != "get") {
        lateinit var requestBody: RequestBody
        val encoder = HttpConfig.getContentEncoder(contentType)
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
      val okHttpClient = if (followRedirect) OkHttpUtil.REQUEST_REDIRECT_CLIENT.getOkHttpClient() else REQUEST_CLIENT.getOkHttpClient()
      val response = okHttpClient.newCall(request).execute()
      result["code"] = response.code()
      val headers = response.headers().toMultimap()
      val headerJson = JSONObject()
      for ((key,value) in headers){
        headerJson[key] = value
      }
      result["headers"] = headerJson
      val decoder = HttpConfig.getAcceptDecoder(accept)
      var returnData :Any? = null
      decoder?.let {
        returnData = it.handleResponse(response)
      }
      result["data"] = returnData
      return result
    }catch (e:Exception){
      return parseException(e)
    }
  }

  /**
   * 处理网络请求出错
   */
  private fun parseException(exception: Exception):JSONObject{
    val result = JSONObject()
    result["data"] = exception.message
    when(exception){
      is SSLPeerUnverifiedException ->{
        result["code"] = HttpConstants.HTTP_ERROR_SSL_PEER_EXCEPTION
      }
      is SSLHandshakeException ->{
        result["code"] = HttpConstants.HTTP_ERROR_SSL_HAND_SHAKE_EXCEPTION
      }
      is FileNotFoundException ->{
        result["code"] = HttpConstants.HTTP_ERROR_FILE_NOT_FOUND_EXCEPTION
      }
      is UnknownHostException ->{
        result["code"] = HttpConstants.HTTP_ERROR_UNKNOWN_HOST_EXCEPTION
      }
      is ConnectTimeoutException ->{
        result["code"] = HttpConstants.HTTP_ERROR_CONNECT_TIMEOUT_EXCEPTION
      }
      is SocketTimeoutException ->{
        result["code"] = HttpConstants.HTTP_ERROR_SOCKET_TIMEOUT_EXCEPTION
      }
      is IOException ->{
        result["code"] = HttpConstants.HTTP_ERROR_IO_EXCEPTION
      }
      is SSLException ->{
        result["code"] = HttpConstants.HTTP_ERROR_SSL_EXCEPTION
      }
      else ->{
        result["code"] = HttpConstants.HTTP_ERROR_UNKNOWN
      }
    }
    return result
  }

  private fun isNetworkOn():Boolean{
    val connectManager = ApplicationContext.CONTEXT.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val mNetworkInfo = connectManager?.activeNetworkInfo
    return mNetworkInfo?.isConnected?:false
  }

}