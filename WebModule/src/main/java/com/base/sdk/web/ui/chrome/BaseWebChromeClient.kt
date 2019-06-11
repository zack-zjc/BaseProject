package com.base.sdk.web.ui.chrome

import android.webkit.WebView
import com.base.sdk.web.ui.ActBaseWebView
import java.lang.ref.WeakReference

/**
 * author:zack
 * Date:2019/6/11
 * Description:webChromeClient
 */
open class BaseWebChromeClient(mActivity: ActBaseWebView,mWebView: WebView?) :BaseWebVideoChromeClient(VideoChromeInterface(mActivity,mWebView)) {

  private val activityWeakReference = WeakReference(mActivity)

  /**
   * 获取网页加载进度
   */
  override fun onProgressChanged(view: WebView?, newProgress: Int) {
    super.onProgressChanged(view, newProgress)
    activityWeakReference.get()?.initProgress(newProgress)
  }

  /**
   * 获取网页标题
   */
  override fun onReceivedTitle(view: WebView?,title: String?) {
    super.onReceivedTitle(view, title)
    title?.let {
      activityWeakReference.get()?.initTitle(it)
    }
  }


}