package com.base.sdk.weex

import android.content.Context
import com.taobao.weex.adapter.IDrawableLoader
import com.taobao.weex.adapter.IWXHttpAdapter
import com.taobao.weex.adapter.IWXImgLoaderAdapter
import com.taobao.weex.adapter.IWXJSExceptionAdapter
import com.taobao.weex.common.WXModule

/**
 * author:zack
 * Date:2019/3/27
 * Description:weex开放的接口
 */
interface WeexInterface {

  /**
   * 图片加载器
   */
  fun getImageAdapter(): IWXImgLoaderAdapter

  /**
   * drawable加载器
   */
  fun getDrawableAdapter(context: Context):IDrawableLoader

  /**
   * 展示错误的adapter
   */
  fun getExceptionAdapter(): IWXJSExceptionAdapter

  /**
   * 获取实现请求的adapter
   */
  fun getHttpAdapter(): IWXHttpAdapter

  /**
   * 获取注册的wx请求的module名称
   */
  fun getWxStreamModuleName():String

  /**
   * 获取注册的wx请求的module
   */
  fun getWxStreamModule():Class<out WXModule>

  /**
   * 获取注册的wxevent的module名称
   */
  fun getWxEventModuleName():String

  /**
   * 获取注册的wxevent的module
   */
  fun getWxEventModule():Class<out WXModule>

  /**
   * 获取注册的wx界面处理的module名称
   */
  fun getWxPageModuleName():String

  /**
   * 获取注册的wx界面处理的module
   */
  fun getWxPageModule():Class<out WXModule>

}