package com.base.sdk.web.entity

import java.io.Serializable

/**
 * author:zack
 * Date:2019/3/28
 * Description:分享数据的entity
 */
data class ShareEntity(val title:String?="",val message:String?="",val imagePath :String?="",val htmlPath:String?=""):Serializable