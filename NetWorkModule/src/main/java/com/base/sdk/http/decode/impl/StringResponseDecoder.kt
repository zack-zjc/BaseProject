package com.base.sdk.http.decode.impl

import com.base.sdk.http.content.Accept
import com.base.sdk.http.decode.ResponseDecoder
import okhttp3.Response

/**
 * author:zack
 * Date:2019/3/25
 * Description:处理string返回数据的decoder
 */
class StringResponseDecoder : ResponseDecoder<String>{

  override fun handleResponse(response: Response) :String {
    return response.body()?.string() ?:""
  }

  override fun canProcessResponse(accept: String): Boolean {
    return accept == Accept.APPLICATION_JSON
  }

}