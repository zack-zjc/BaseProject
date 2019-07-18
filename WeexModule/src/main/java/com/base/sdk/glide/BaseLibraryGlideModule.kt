package com.base.sdk.glide

import android.content.Context
import com.base.sdk.glide.OkHttpUrlLoader.Factory
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 * author:zack
 * Date:2019/4/16
 * Description:okHttp请求的glideUrl
 */
@GlideModule
open class BaseLibraryGlideModule : LibraryGlideModule() {

  /**
   * 替换glideUrl
   */
  override fun registerComponents(context: Context,glide: Glide,registry: Registry) {
    val builder = OkHttpClient().newBuilder()
        //默认glide读取timeout 20s
        .readTimeout(20000, TimeUnit.MILLISECONDS)
        //默认glide连接timeout 15s
        .connectTimeout(15000, TimeUnit.MILLISECONDS)
        //默认glide写入timeout 20s
        .writeTimeout(20000, TimeUnit.MILLISECONDS)
    registry.replace(GlideUrl::class.java, InputStream::class.java,Factory(builder.build()))
  }


}