package com.base.sdk.http.util

import com.base.sdk.http.util.CustomLoggingInterceptor
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

/**
 * author:zack
 * Date:2019/3/25
 * Description:网络请求类库
 */
enum class OkHttpUtil(followRedirect: Boolean) {

  //非重定向普通请求client
  REQUEST_CLIENT(false),

  //重定向请求client支持redirect
  REQUEST_REDIRECT_CLIENT(true);

  //httpClient实例
  private val okHttpClient: OkHttpClient

  init {
    val builder = OkHttpClient.Builder()
        .readTimeout(20000L, TimeUnit.MILLISECONDS)
        .writeTimeout(20000L, TimeUnit.MILLISECONDS)
        .connectTimeout(15000L, TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(true)
        .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .connectionPool(ConnectionPool(5, 15, SECONDS))
        .followRedirects(followRedirect)
        .followSslRedirects(followRedirect)
        .addInterceptor(CustomLoggingInterceptor())
    okHttpClient = builder.build()
  }

  /**
   * 获取okHttpClient实例
   */
  fun getOkHttpClient() :OkHttpClient = okHttpClient

}