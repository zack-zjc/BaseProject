package com.base.sdk.http

import android.content.Context

/**
 * author:zack
 * Date:2019/2/28
 * Description:当前进程的AppContext
 */
object ApplicationContext {

  val CONTEXT : Context by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    val mActivityThread = Class.forName("android.app.ActivityThread")
    val applicationMethod = mActivityThread.getDeclaredMethod("currentApplication")
    applicationMethod.invoke(null) as Context
  }

}