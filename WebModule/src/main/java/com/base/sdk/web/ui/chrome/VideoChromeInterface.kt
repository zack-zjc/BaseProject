package com.base.sdk.web.ui.chrome

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebView
import android.widget.FrameLayout

/**
 * author:zack
 * Date:2019/4/17
 * Description:支持视频播放的client
 */
open class VideoChromeInterface(private val activity: Activity,private val webView:WebView?) : IVideo{

  private var mMovieView :View? = null
  private var mMovieParentView :ViewGroup? = null
  private var mCallback:CustomViewCallback? = null

  /**
   * 展示视频
   */
  override fun onShowCustomView(view: View,callback: CustomViewCallback) {
    if (activity.isFinishing) return
    if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    // 保存当前屏幕的常亮
    activity.window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    if (mMovieView != null) { //当前已经是全屏了
      callback.onCustomViewHidden()
      return
    }
    webView?.visibility = View.GONE
    if (mMovieParentView == null) {
      val mDecorView = activity.window.decorView as FrameLayout
      mMovieParentView = FrameLayout(activity)
      mMovieParentView?.setBackgroundColor(Color.BLACK)
      mDecorView.addView(mMovieParentView)
    }
    this.mCallback = callback
    this.mMovieView = view
    mMovieParentView?.addView(mMovieView)
    mMovieParentView?.visibility = View.VISIBLE
  }

  /**
   * 隐藏视频
   */
  override fun onHideCustomView() {
    if (mMovieView == null) return
    if (activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
      activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    mMovieView?.visibility = View.GONE
    mMovieParentView?.removeView(mMovieView)
    mMovieParentView?.visibility = View.GONE
    mCallback?.onCustomViewHidden()
    mMovieView = null
    webView?.visibility = View.VISIBLE
  }

  /**
   * 当前是否是视频全屏状态
   */
  override fun isVideoState(): Boolean  = mMovieView != null

}