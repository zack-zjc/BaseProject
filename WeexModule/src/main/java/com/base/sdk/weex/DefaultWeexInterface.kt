package com.base.sdk.weex

import android.content.Context
import com.base.sdk.weex.adapter.WXLCDrawableAdapter
import com.base.sdk.weex.adapter.WXLCHttpAdapter
import com.base.sdk.weex.adapter.WXLCImageAdapter
import com.base.sdk.weex.adapter.WXLCJSExceptionAdapter
import com.base.sdk.weex.module.WXLCEventModule
import com.base.sdk.weex.module.WXLCPageModule
import com.base.sdk.weex.module.WXLCStreamModule
import com.taobao.weex.adapter.IDrawableLoader
import com.taobao.weex.adapter.IWXHttpAdapter
import com.taobao.weex.adapter.IWXImgLoaderAdapter
import com.taobao.weex.adapter.IWXJSExceptionAdapter
import com.taobao.weex.common.WXModule

/**
 * author:zack
 * Date:2019/3/27
 * Description:默认的weex注册interface
 */
open class DefaultWeexInterface : WeexInterface {

  override fun getImageAdapter(): IWXImgLoaderAdapter = WXLCImageAdapter()

  override fun getDrawableAdapter(context: Context): IDrawableLoader = WXLCDrawableAdapter(context)

  override fun getExceptionAdapter(): IWXJSExceptionAdapter = WXLCJSExceptionAdapter()

  override fun getHttpAdapter(): IWXHttpAdapter = WXLCHttpAdapter()

  override fun getWxStreamModuleName(): String = "lc_fetch"

  override fun getWxStreamModule(): Class<out WXModule> = WXLCStreamModule::class.java

  override fun getWxEventModuleName(): String  = "lc_function"

  override fun getWxEventModule(): Class<out WXModule> = WXLCEventModule::class.java

  override fun getWxPageModuleName(): String = "lc_page"

  override fun getWxPageModule(): Class<out WXModule> = WXLCPageModule::class.java

}