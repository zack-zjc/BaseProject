package com.base.sdk.web.ui.chrome

import android.view.View
import android.webkit.WebChromeClient

/**
 * author:zack
 * Date:2019/4/17
 * Description:基础的client
 */
open class BaseWebVideoChromeClient(private val videoInterface:IVideo?=null) : WebChromeClient() {

  override fun onShowCustomView(view: View?,callback: CustomViewCallback?) {
    super.onShowCustomView(view, callback)
    if (view != null && callback != null){
      videoInterface?.onShowCustomView(view,callback)
    }
  }

  override fun onHideCustomView() {
    super.onHideCustomView()
    videoInterface?.onHideCustomView()
  }

}