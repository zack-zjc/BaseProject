package com.base.sdk.http.httpclient.log

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

/**
 * author:zack
 * Date:2019/3/28
 * Description:展示网络请求的log
 */
class CustomLoggingInterceptor : Interceptor{

  override fun intercept(chain: Chain): Response {
    val request = chain.request()
    Log.e("HttpLoggingInterceptor","request-${request.url()}")
    val headers = request.headers()
    Log.e("HttpLoggingInterceptor","headers-$headers")
    val response: Response
    try {
      response = chain.proceed(request)
    } catch (e: Exception) {
      Log.e("HttpLoggingInterceptor","HTTP FAILED-$e")
      throw e
    }
    Log.e("HttpLoggingInterceptor","<-- -${response.code()}.${response.message()}-${response.request().url()}")
    val responseHeaders = response.headers()
    Log.e("HttpLoggingInterceptor","headers-$responseHeaders")
    return response
  }

}