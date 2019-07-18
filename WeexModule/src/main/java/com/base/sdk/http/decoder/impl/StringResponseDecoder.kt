package com.base.sdk.http.decoder.impl

import com.base.sdk.http.util.HeaderType
import com.base.sdk.http.decoder.ResponseDecoder
import okhttp3.Response

/**
 * author:zack
 * Date:2019/3/25
 * Description:处理string返回数据的decoder
 */
class StringResponseDecoder : ResponseDecoder<String> {

  override fun handleResponse(response: Response) :String {
    return response.body()?.string() ?:""
  }

  override fun canProcessResponse(accept: String): Boolean {
    return accept == HeaderType.APPLICATION_JSON
  }

}