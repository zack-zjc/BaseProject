package com.base.sdk.share.qq

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformConfig
import com.base.sdk.share.platform.SharePlatformInterface
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError

/**
 * author:zack
 * Date:2019/3/5
 * Description:分享到qq好友
 */
class TencentFriendUtil : SharePlatformInterface , IUiListener {

  private var callback:Callback? = null

  companion object {
    val instance by lazy { TencentFriendUtil() }
  }

  override fun configPlatform(config: PlatformConfig) {
    TencentUtil.instance.configQQ(config)
  }

  override fun isPlatformInstalled() :Boolean = TencentUtil.instance.isPlatformInstalled()

  override fun registerApp(context: Activity) {
    TencentUtil.instance.registerApp(context)
  }

  override fun share(shareEntity: ShareEntity,callback: Callback?) {
    this.callback = callback
    TencentUtil.instance.shareFriend(shareEntity,this)
  }

  override fun handleIntent(requestCode: Int,resultCode: Int,data: Intent?) {
    if (requestCode == Constants.REQUEST_OLD_SHARE || requestCode == Constants.REQUEST_QQ_SHARE){
      Tencent.onActivityResultData(requestCode, resultCode, data, this)
    }
  }

  override fun onComplete(p0: Any?) {
    callback?.onSuccess(this)
  }

  override fun onCancel() {
    callback?.onUserCancel(this)
  }

  override fun onError(p0: UiError?) {
    Log.e("share",p0.toString())
    callback?.onFail(this)
  }

}
