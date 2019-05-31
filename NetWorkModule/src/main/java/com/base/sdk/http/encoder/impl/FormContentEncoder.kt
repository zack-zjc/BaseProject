package com.base.sdk.http.encoder.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.content.ContentType
import com.base.sdk.http.encoder.ContentEncoder
import okhttp3.FormBody
import okhttp3.RequestBody

/**
 * author:zack
 * Date:2019/3/25
 * Description:form字段的body处理
 */
class FormContentEncoder : ContentEncoder {

  override fun encoder(body: Any): RequestBody {
    val formBuilder = FormBody.Builder()
    val jsonBody = body as? JSONObject ?: (JSON.parseObject(body.toString())?: JSONObject())
    for ((key,value) in jsonBody) {
      formBuilder.add(key, value.toString())
    }
    return formBuilder.build()
  }

  override fun canProcessContent(contentType: String): Boolean {
    return contentType == ContentType.APPLICATION_X_WWW_FORM_URLENCODED
  }

}