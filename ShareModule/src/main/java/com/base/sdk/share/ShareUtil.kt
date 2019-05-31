package com.base.sdk.share

import android.app.Activity
import android.content.Intent
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.callback.DefaultCallback
import com.base.sdk.share.config.ShareConfig
import com.base.sdk.share.dialog.ShareDialog
import com.base.sdk.share.entity.ShareEntity

/**
 * author:zack
 * Date:2019/3/26
 * Description:分享的util
 */
object ShareUtil {

  /**
   * 展示分享的dialog
   * 自定义可修改dialog目录下代码，其他平台为已配置好接口直接调用
   */
  fun share(activity: Activity,title:String?="",message:String?="",imagePath :String?="",htmlPath:String?="",callback: Callback=DefaultCallback()){
    val shareEntity = ShareEntity(title = title,message = message,imagePath = imagePath,htmlPath = htmlPath)
    ShareDialog.showShareDialog(activity,shareEntity, ShareConfig.sharePlatformList.toTypedArray(),callback)
  }

  /**
   * 分享返回时调用处理返回数据
   */
  fun handleIntent(requestCode: Int,resultCode: Int,data : Intent?){
    for (platform in ShareConfig.sharePlatformList){
      platform.getSharePlatform().handleIntent(requestCode,resultCode,data)
    }
  }



}