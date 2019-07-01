package com.base.sdk.weex.module

import android.graphics.Color
import com.base.sdk.weex.ui.ActWeexPage
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXModule

/**
 * author:zack
 * Date:2019/3/27
 * Description:界面处理的module
 */
open class WXLCPageModule : WXModule(){

  /**
   * 是否隐藏标题栏
   */
  @JSMethod(uiThread = true)
  open fun hideNavigatorBar(hide:Boolean) {
    val context = mWXSDKInstance.context
    if (context is ActWeexPage) {
      context.hideToolbar(hide)
    }
  }

  /**
   * title:页面的标题
   */
  @JSMethod(uiThread = true)
  open fun setTitle(title: String) {
    val context = mWXSDKInstance.context
    if (context is ActWeexPage) {
      context.setPageTitle(title)
    }
  }

  /**
   * menuText:页面的menu
   */
  @JSMethod(uiThread = true)
  open fun setMenuText(menuText: String,callback:JSCallback) {
    val context = mWXSDKInstance.context
    if (context is ActWeexPage) {
      context.setMenuText(menuText,callback)
    }
  }

  /**
   * menuText:页面的menu
   * menuIconUrl:图片的路径
   */
  @JSMethod(uiThread = true)
  open fun setMenuIcon(menuIconUrl: String,callback:JSCallback) {
    val context = mWXSDKInstance.context
    if (context is ActWeexPage) {
      context.setMenuIcon(menuIconUrl,callback)
    }
  }

  /**
   * 设置界面toolbar的样式
   * isDark:界面toolbar是否是深色系颜色
   * statusBarColor：设置toolbar的背景颜色
   */
  @JSMethod(uiThread = true)
  open fun setTitleTheme(option:Map<String,Any>){
    val isDarkStatus = option["isDark"].toString().toBoolean()
    val statusColor = Color.parseColor(option["statusBarColor"].toString())
    val context = mWXSDKInstance.context
    if (context is ActWeexPage) {
      context.setStatusTheme(statusColor,isDarkStatus)
    }
  }

  /**
   * 从当前页面打开一个新的页面
   */
  @JSMethod(uiThread = true)
  open fun openUrl(option:Map<String,Any>){
    val pageUrl = option["url"].toString()
    val context = mWXSDKInstance.context
    if (context is ActWeexPage) {
      context.openUrl(pageUrl)
    }
  }



}