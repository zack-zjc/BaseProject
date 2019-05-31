package com.base.sdk.share.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.R
import java.io.Serializable

/**
 * author:zack
 * Date:2019/3/4
 * Description:分享数据的entity
 */
data class ShareEntity(val title:String?="",val message:String?="",val imagePath :String?="",val htmlPath:String?="",val extraInfo :Any?=null) :Serializable {

  /**
   * 分享文字为空
   */
  fun isShareTitleEmpty() : Boolean = title.isNullOrEmpty()

  /**
   * 获取分享文字
   */
  fun getShareTitleText() :String = title?:""

  /**
   * 分享文字为空
   */
  fun isShareDescriptionEmpty() : Boolean = message.isNullOrEmpty()

  /**
   * 获取分享文字
   */
  fun getShareDescriptionText() :String = message?:""

  /**
   * 分享图片为空
   */
  fun isShareImageEmpty() : Boolean = imagePath.isNullOrEmpty()

  /**
   * 获取图片bitmap
   * path为本地地址
   */
  fun getShareImageUrl() :String = imagePath?:""

  /**
   * 获取图片bitmap
   * path为本地地址
   */
  fun getShareImageBitmap() :Bitmap{
    try {
      return BitmapFactory.decodeFile(imagePath)
    }catch (e:Exception){
      e.printStackTrace()
    }
    return BitmapFactory.decodeResource(ApplicationContext.CONTEXT.resources, R.mipmap.ic_launcher)
  }

  /**
   * 分享网页为空
   */
  fun isShareHtmlEmpty() : Boolean = htmlPath.isNullOrEmpty()

  /**
   * 获取网页分享地址
   */
  fun getShareHtml() :String = htmlPath?:""

  /**
   * 获取自定义的扩展属性
   */
  fun getExtra() :Any? = extraInfo

}