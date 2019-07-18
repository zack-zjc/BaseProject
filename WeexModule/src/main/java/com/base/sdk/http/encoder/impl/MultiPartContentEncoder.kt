package com.base.sdk.http.encoder.impl

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.base.sdk.http.util.HeaderType
import com.base.sdk.http.encoder.ContentEncoder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * author:zack
 * Date:2019/3/25
 * Description:文件上传处理类
 */
class MultiPartContentEncoder : ContentEncoder{

  override fun encoder(body: Any): RequestBody {
    val multiBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
    val jsonBody = body as? JSONObject ?: (JSON.parseObject(body.toString())?: JSONObject())
    for ((key,value) in jsonBody) {
      var jsonArrayValue :JSONArray? = null
      if (key == "file"){
        try {
          jsonArrayValue = JSONArray.parseArray(value.toString())
        }catch (e:Exception){
          e.printStackTrace()
        }
      }
      if (key == "file" && jsonArrayValue != null){
        for (json in jsonArrayValue){
          val path = parseFilePath(json)
          if (path.isNotEmpty()){
            multiBuilder.addFormDataPart("file", File(path).name,
                RequestBody.create(MediaType.parse("multipart/form-data"), File(path)))
          }
        }
      }else if(value is String){
        multiBuilder.addFormDataPart(key, value.toString())
      }else{
        multiBuilder.addFormDataPart(key,null,RequestBody.create(MediaType.parse(HeaderType.APPLICATION_JSON), JSONObject.toJSONString(value)))
      }
    }
    return multiBuilder.build()
  }

  private fun parseFilePath(content:Any?):String{
    val jsonBody = content as? JSONObject ?: (JSON.parseObject(content.toString())?: JSONObject())
    var filePath = ""
    for ((key,value) in jsonBody) {
      if (File(value.toString()).exists()){
        filePath = value.toString()
      }
    }
    return filePath
  }

  override fun canProcessContent(contentType: String): Boolean {
    return contentType == HeaderType.MULTIPAR_FORM_DATA
  }

}