package com.base.sdk.http.httpclient

import okhttp3.OkHttpClient

/**
 * author:zack
 * Date:2019/3/27
 * Description:自定义设置okHttpClient
 */
interface ClientSettingInterface {

  /**
   * 设置okHttpClient的设置
   */
  fun setOkHttpClientBuilder(builder:OkHttpClient.Builder):OkHttpClient.Builder

}