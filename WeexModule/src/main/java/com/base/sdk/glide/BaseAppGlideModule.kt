package com.base.sdk.glide

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.*
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * author:zack
 * Date:2019/2/28
 * Description:glide初始化类
 */
@GlideModule
open class BaseAppGlideModule : AppGlideModule() {

  override fun isManifestParsingEnabled(): Boolean = false

  override fun applyOptions(context: Context,builder: GlideBuilder) {
    /*
     * 使用PREFER_RGB_565只占用PREFER_ARGB_8888一半内存但存在透明颜色和纹理等颜色出现加载异常的情况
     * for example u选加载中图片gif透明背景变成黑色
     */
    builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
    val calculator = MemorySizeCalculator.Builder(context).build()
    val defaultMemoryCacheSize = calculator.memoryCacheSize
    val defaultBitmapPoolSize = calculator.bitmapPoolSize
    val customMemoryCacheSize = (1.2 * defaultMemoryCacheSize).toInt()
    val customBitmapPoolSize = (1.2 * defaultBitmapPoolSize).toInt()
    val memoryCache = LruResourceCache(customMemoryCacheSize.toLong())
    builder.setMemoryCache(memoryCache)
    builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))
    val diskCache: DiskCache.Factory = if (checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ExternalPreferredCacheDiskCacheFactory(context)
    } else {
      InternalCacheDiskCacheFactory(context)
    }
    builder.setDiskCache(diskCache)
    builder.setSourceExecutor(GlideExecutor.newSourceExecutor())
    builder.setDiskCacheExecutor(GlideExecutor.newSourceExecutor())
  }

  /**
   * 是否有相关权限
   */
  private fun checkSelfPermission(context: Context,permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }else{
      val packageManager = context.packageManager
      packageManager.checkPermission(permission, context.packageName) == PackageManager.PERMISSION_GRANTED
    }
  }

}