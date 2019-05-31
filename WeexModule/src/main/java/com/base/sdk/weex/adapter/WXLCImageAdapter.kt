package com.base.sdk.weex.adapter

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.taobao.weex.adapter.IWXImgLoaderAdapter
import com.taobao.weex.common.WXImageStrategy
import com.taobao.weex.dom.WXImageQuality
import java.io.File

/**
 * author:zack
 * Date:2019/3/27
 * Description:图片加载类
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
        Glide.with(context).asDrawable().load(imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).listener(object :
            RequestListener<Drawable> {

          override fun onResourceReady(resource: Drawable?,model: Any?,target: Target<Drawable>?,
            dataSource: DataSource?,isFirstResource: Boolean): Boolean {
            try {
              if (strategy != null && strategy.imageListener != null) {
                val extra = mutableMapOf<String,Int>()
                if (resource is BitmapDrawable){
                  extra["naturalWidth"] = resource.bitmap.width
                  extra["naturalHeight"] = resource.bitmap.height
                }
                strategy.imageListener.onImageFinish(url, view, true, extra)
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }
            return false
          }

          override fun onLoadFailed(e: GlideException?,model: Any?,target: Target<Drawable>?,
            isFirstResource: Boolean): Boolean {
            e?.printStackTrace()
            it.setImageDrawable(null)
            return false
          }
        }).into(it)
      }
    }
  }

}