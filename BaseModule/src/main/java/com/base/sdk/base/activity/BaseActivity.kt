package com.base.sdk.base.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.base.sdk.base.R
import com.base.sdk.base.util.DeviceUtil
import me.yokeyword.fragmentation_swipeback.SwipeBackActivity

/**
 * author:zack
 * Date:2019/3/27
 * Description:基础的activity
 * 只包含状态栏穿透设置和状态栏与navBar颜色设置
 */
open class BaseActivity : SwipeBackActivity() {

  private var firstEnter = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initWindowFeature()
    animationFlipIn()
  }

  /**
   * 设置窗口的穿透以及statusBar和NavigationBar的状态
   */
  private fun initWindowFeature() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or  WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
    initStatusBar()
    initNavigationBar()
  }

  /**
   * 设置statusbar颜色
   * @return statusBar的颜色值
   */
  protected open fun statusBarColor():Int = Color.TRANSPARENT

  /**
   * 是否需要设置浅色系statusBar文字
   * @return true设置浅色系false不设置浅色系
   * * true文字基本为黑色，false文字为白色
   */
  protected open fun isLightStatusBar(): Boolean = true

  /**
   * 初始化statusBarde为浅色系并设置statusBar的颜色
   * 默认颜色为透明
   */
  private fun initStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (DeviceUtil.isXiaoMi()) {
        DeviceUtil.setMIUIStatusBarLightMode(window, isLightStatusBar())
      } else if (DeviceUtil.isFlyme()) {
        DeviceUtil.setFlymeStatusBarLightMode(window, isLightStatusBar())
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLightStatusBar()) {
        //设置浅色系的statusBar
        val flag = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
      }
      initStatusBarColor(statusBarColor())
    }
  }

  /**
   * 设置statusBar的颜色
   */
  protected fun initStatusBarColor(color:Int){
    window.statusBarColor = color
  }

  /**
   * 设置statusbar颜色
   * @return statusBar的颜色值
   */
  protected open fun navigationBarColor():Int? = null

  /**
   * 设置NavigationBar的颜色
   */
  protected fun initNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && navigationBarColor() != null) {
      window.navigationBarColor = navigationBarColor()!!
    }
  }

  /**
   * 界面获取焦点反馈操作
   * @param hasFocus 当前界面是否获取焦点
   */
  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus && firstEnter) {
      firstEnter = false
      onLazyInit()
    }
  }

  /**
   * 界面首次获取焦点展示
   * 可用于部分view懒加载初始化
   */
  protected open fun onLazyInit() {

  }

  /***********************************过渡动画相关 ******************************************************/
  /**
   * 是否徐亚进入动画
   */
  protected open fun isNeedFlipInAni(): Boolean = true

  /**
   * 是否需要退出动画
   */
  protected open fun isNeedFlipOut(): Boolean = true

  /**
   * 进入动画
   */
  protected open fun getFlipInAni(): IntArray {
    return intArrayOf(R.anim.anim_base_window_in, R.anim.anim_base_window_out)
  }

  /**
   * 退出动画
   */
  protected open fun getFlipOutAni(): IntArray {
    return intArrayOf(R.anim.anim_base_window_close_in, R.anim.anim_base_window_close_out)
  }

  /**
   * 界面进入动画
   */
  protected open fun animationFlipIn() {
    if (isNeedFlipInAni()) {
      val flipAni = getFlipInAni()
      overridePendingTransition(flipAni[0], flipAni[1])
    }
  }

  /**
   * 界面退出动画
   */
  protected open fun animationFlipOut() {
    if (isNeedFlipOut()) {
      val flipAni = getFlipOutAni()
      overridePendingTransition(flipAni[0], flipAni[1])
    }
  }

  /**
   * 结束界面执行动画
   */
  override fun finish() {
    super.finish()
    animationFlipOut()
  }

}