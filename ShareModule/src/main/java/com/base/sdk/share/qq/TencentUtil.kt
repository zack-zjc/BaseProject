package com.base.sdk.share.qq

import android.app.Activity
import android.os.Bundle
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformConfig
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzonePublish
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import java.lang.ref.WeakReference

/**
 * author:zack
 * Date:2019/3/5
 * Description:qq分享的包
 * 空间不支持音乐，朋友不支持视频
 * 无图片可能导致无法分享
 */
class TencentUtil {

  private var mTencent :Tencent? = null

  private var mActivityReference :WeakReference<Activity>? = null

  companion object {
    val instance by lazy { TencentUtil() }
  }

  /**
   * 配置qq平台
   */
  fun configQQ(config: PlatformConfig){
    if (mTencent == null){
      mTencent = Tencent.createInstance(config.appId, ApplicationContext.CONTEXT)
    }
  }

  /**
   * 获取对象
   */
  fun getTencent():Tencent? = mTencent

  /**
   * qq是否安装
   */
  fun isPlatformInstalled() = mTencent?.isQQInstalled(ApplicationContext.CONTEXT)?:false

  /**
   * 注册qq
   */
  fun registerApp(context: Activity) {
    mActivityReference = WeakReference(context)
  }

  /**
   * 分享到QQ空间
   * 支持分享视频，图片，文字，网页
   * 不支持音乐
   */
  fun shareSpace(shareEntity: ShareEntity,listener: IUiListener) {
    val bundle = Bundle()
    if (!shareEntity.isShareHtmlEmpty()){
      //分享类型
      bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
      bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareEntity.getShareTitleText())//必填
      bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,shareEntity.getShareDescriptionText())//选填
      bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareEntity.getShareHtml())//必填
      if (!shareEntity.isShareImageEmpty()){
        val arrayList = ArrayList<String>()
        arrayList.add(shareEntity.getShareImageUrl())
        //api显示可本地可网络
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList)
      }
      if (mActivityReference != null && mActivityReference?.get() != null){
        mTencent?.shareToQzone(mActivityReference?.get(), bundle, listener)
      }
    }else if (!shareEntity.isShareImageEmpty()){
      bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
      if (!shareEntity.isShareDescriptionEmpty()){
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,shareEntity.getShareDescriptionText())
      }
      val arrayList = ArrayList<String>()
      arrayList.add(shareEntity.getShareImageUrl())
      //只支持本地图片
      bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList)
      if (mActivityReference != null && mActivityReference?.get() != null){
        mTencent?.publishToQzone(mActivityReference?.get(), bundle, listener)
      }
    }else if (!shareEntity.isShareDescriptionEmpty() || !shareEntity.isShareTitleEmpty()){
      bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD)
      val text = if (!shareEntity.isShareDescriptionEmpty()) shareEntity.getShareDescriptionText() else shareEntity.getShareTitleText()
      bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,text)
      if (!shareEntity.isShareImageEmpty()){
        val arrayList = ArrayList<String>()
        arrayList.add(shareEntity.getShareImageUrl())
        //只支持本地图片
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList)
      }
      if (mActivityReference != null && mActivityReference?.get() != null){
        mTencent?.publishToQzone(mActivityReference?.get(), bundle, listener)
      }
    }
  }

  /**
   * 分享到好友
   * 支持分享图片，音乐，文字，网页
   * 不支持视频
   */
  fun shareFriend(shareEntity: ShareEntity,listener: IUiListener){
    val bundle = Bundle()
    if (!shareEntity.isShareHtmlEmpty()){
      //分享的标题。不能为空
      bundle.putString(QQShare.SHARE_TO_QQ_TITLE, shareEntity.getShareTitleText())
      //分享的消息摘要，最长50个字
      bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareEntity.getShareDescriptionText())
      if (!shareEntity.isShareImageEmpty()){
        //分享图片的URL或者本地路径
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareEntity.getShareImageUrl())
      }
      //这条分享消息被好友点击后的跳转URL。不能为空
      bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareEntity.getShareHtml())
      bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
    }else if (!shareEntity.isShareImageEmpty()){
      //分享的图片URL
      bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareEntity.getShareImageUrl())
      ////分享图片的URL或者本地路径
      bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareEntity.getShareImageUrl())
      bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
    }
    if (mActivityReference != null && mActivityReference?.get() != null){
      mTencent?.shareToQQ(mActivityReference?.get(), bundle, listener)
    }
  }

}