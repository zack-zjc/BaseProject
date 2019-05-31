package com.base.sdk.http.encoder.impl

import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.content.ContentType
import com.base.sdk.http.encoder.ContentEncoder
import okhttp3.MediaType
import okhttp3.RequestBody

/**
 * author:zack
 * Date:2019/3/25
 * Description:json字段的body处理
 */
class JsonContentEncoder : ContentEncoder{

  override fun encoder(body: Any): RequestBody {
    return RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSONString(body))
  }

  override fun canProcessContent(contentType: String): Boolean {
    return contentType == ContentType.APPLICATION_JSON
  }

}