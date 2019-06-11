package com.base.sdk.web.ui.chrome

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebView
import com.base.sdk.web.R

/**
 * author:zack
 * Date:2019/4/17
 * Description:支持视频播放的client
 */
open class VideoChromeInterface(private val activity: Activity,private val webView:WebView?) : IVideo{

  private var mMovieView :View? = null
  private var mPreWebContainer :View? = null
  private var mCallback:CustomViewCallback? = null

  /**
   * 展示视频
   */
  override fun onShowCustomView(view: View,callback: CustomViewCallback) {
    if (activity.isFinishing) return
    if (mMovieView != null) { //当前已经是全屏了
      callback.onCustomViewHidden()
      return
    }
    val containerView = activity.findViewById<ViewGroup?>(R.id.id_web_root_view)
    if (containerView != null){
      mPreWebContainer = containerView.getChildAt(0)
      containerView.removeViewAt(0)
      this.mMovieView = view
      containerView.addView(mMovieView,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
      this.mCallback = callback
      // 保存当前屏幕的常亮
      activity.window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
  }

  /**
   * 隐藏视频
   */
  override fun onHideCustomView() {
    if (mMovieView == null) return
    val containerView = activity.findViewById<ViewGroup?>(R.id.id_web_root_view)
    if (containerView != null){
      mMovieView?.visibility = View.GONE
      containerView.removeView(mMovieView)
      containerView.addView(mPreWebContainer,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
      mCallback?.onCustomViewHidden()
      mMovieView = null
    }
  }

  /**
   * 当前是否是视频全屏状态
   */
  override fun isVideoState(): Boolean  = mMovieView != null

}