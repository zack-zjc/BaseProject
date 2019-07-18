package com.base.sdk.weex.adapter

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.taobao.weex.adapter.IWXImgLoaderAdapter
import com.taobao.weex.common.WXImageStrategy
import com.taobao.weex.dom.WXImageQuality
import java.io.File

/**
 * author:zack
 * Date:2019/3/27
 * Description:图片加载类
 * Tips:onImageFinish回调获取图片的宽高返回需要在imageview设置过图片资源获取
 */
open class WXLCImageAdapter : IWXImgLoaderAdapter{

  override fun setImage(url: String?,view: ImageView?,quality: WXImageQuality?,strategy: WXImageStrategy?) {
    if (url.isNullOrEmpty()){
      view?.setImageDrawable(null)
      return
    }
    view?.let {
      var imageUri = Uri.parse(url)
      if (File(url).exists()){
        imageUri = Uri.fromFile(File(url))
      }
      val context = it.context
      if (context is Activity && !context.isDestroyed && !context.isFinishing){
        Glide.with(context).asDrawable().load(imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).into(object : CustomTarget<Drawable>(){

          override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            try {
              it.setImageDrawable(resource)
              if (strategy != null && strategy.imageListener != null) {
                val extra = mutableMapOf<String,Int>()
                if (resource is BitmapDrawable){
                  extra["naturalWidth"] = resource.bitmap.width
                  extra["naturalHeight"] = resource.bitmap.height
                }else if (resource is GifDrawable){
                  extra["naturalWidth"] = resource.intrinsicWidth
                  extra["naturalHeight"] = resource.intrinsicHeight
                  resource.start()
                }
                strategy.imageListener.onImageFinish(url, it, true, extra)
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }

          override fun onLoadCleared(placeholder: Drawable?) {
            it.setImageDrawable(placeholder)
          }

          override fun onLoadFailed(errorDrawable: Drawable?) {
            it.setImageDrawable(null)
          }

        })
      }
    }
  }

}