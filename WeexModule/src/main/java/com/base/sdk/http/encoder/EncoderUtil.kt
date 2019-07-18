package com.base.sdk.http.encoder

import com.base.sdk.http.encoder.impl.FormContentEncoder
import com.base.sdk.http.encoder.impl.JsonContentEncoder
import com.base.sdk.http.encoder.impl.MultiPartContentEncoder

/**
 * @Author zack
 * @Date 2019/7/18
 * @Description 数据encoder对象
 * @Version 1.0
 */
object EncoderUtil {

    private val encoderList = mutableListOf(JsonContentEncoder(),FormContentEncoder(),MultiPartContentEncoder())

    /**
     * 获取encoder
     */
    fun getEncoder(contentType:String):ContentEncoder?{
        for (encoder in encoderList){
            if (encoder.canProcessContent(contentType)){
                return encoder
            }
        }
        return null
    }

    /**
     * 添加encoder
     */
    fun addEncoder(encoder:ContentEncoder){
        encoderList.add(encoder)
    }

}