package com.base.sdk.http.decoder

import com.base.sdk.http.decoder.impl.StringResponseDecoder

/**
 * @Author zack
 * @Date 2019/7/18
 * @Description decoder集合
 * @Version 1.0
 */
object DecoderUtil {

    private val decoderList = mutableListOf<ResponseDecoder<out Any>>(StringResponseDecoder())

    /**
     * 获取encoder
     */
    fun getDecoder(contentType:String): ResponseDecoder<out Any>? {
        for (decoder in decoderList){
            if (decoder.canProcessResponse(contentType)){
                return decoder
            }
        }
        return null
    }

    /**
     * 添加encoder
     */
    fun addDecoder(decoder: ResponseDecoder<out Any>){
        decoderList.add(decoder)
    }

}