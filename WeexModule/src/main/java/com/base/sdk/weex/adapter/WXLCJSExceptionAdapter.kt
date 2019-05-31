package com.base.sdk.weex.adapter

import com.taobao.weex.adapter.IWXJSExceptionAdapter
import com.taobao.weex.common.WXJSExceptionInfo
import com.taobao.weex.utils.WXLogUtils

/**
 * author:zack
 * Date:2019/4/15
 * Description:展示页面错误的adapter
 */
open class WXLCJSExceptionAdapter : IWXJSExceptionAdapter {

  override fun onJSException(exception: WXJSExceptionInfo?) {
    WXLogUtils.d(exception?.toString())
  }
}