package com.base.sdk.http.decoder

import okhttp3.Response

/**
 * author:zack
 * Date:2019/3/25
 * Description:结果返回处理
 */
interface ResponseDecoder<T> {

  /**
   * 处理网络请求返回数据的decoder
   */
   fun handleResponse(response: Response):T

  /**
   * 是否能够处理返回数据
   */
  fun canProcessResponse(accept:String):Boolean

}