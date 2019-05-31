package com.base.sdk.http.encoder

import okhttp3.RequestBody

/**
 * author:zack
 * Date:2019/3/25
 * Description:处理发送的请求的encoder
 */
interface ContentEncoder {

  /**
   * 处理contentBody
   */
  fun encoder(body:Any): RequestBody

  /**
   * 是否能处理对应的contentType
   */
  fun canProcessContent(contentType:String):Boolean

}