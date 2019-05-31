package com.base.sdk.http.httpclient

import okhttp3.OkHttpClient.Builder

/**
 * author:zack
 * Date:2019/3/27
 * Description:默认的client设置
 */
open class DefaultClientSetting : ClientSettingInterface {

  override fun setOkHttpClientBuilder(builder: Builder): Builder {
    return builder
  }

}