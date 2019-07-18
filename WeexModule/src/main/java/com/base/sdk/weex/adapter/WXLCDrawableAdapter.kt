package com.base.sdk.weex.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.taobao.weex.adapter.DrawableStrategy
import com.taobao.weex.adapter.IDrawableLoader
import com.taobao.weex.adapter.IDrawableLoader.DrawableTarget
import java.lang.ref.WeakReference

/**
 * author:zack
 * Date:2019/4/15
 * Description:加载drawable的loader
 */
open class WXLCDrawableAdapter(context: Context) :IDrawableLoader {

  private val contextReference = WeakReference<Context>(context)

  override fun setDrawable(url: String?,drawableTarget: DrawableTarget?,strategy: DrawableStrategy?) {
    contextReference.get()?.let {
      if (drawableTarget != null){
        var request = Glide.with(it).asDrawable().load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
        if (strategy != null){
          request = request.override(strategy.width,strategy.height)
        }
        request.into(DrawableLoadTarget(drawableTarget))
      }
    }
  }

  class DrawableLoadTarget(drawableTarget:DrawableTarget) : CustomTarget<Drawable>() {

    override fun onLoadCleared(placeholder: Drawable?) {
      drawableTarget.get()?.setDrawable(null,true)
    }

    private val drawableTarget =  WeakReference<DrawableTarget>(drawableTarget)

    override fun onLoadStarted(placeholder: Drawable?) {
      drawableTarget.get()?.setDrawable(placeholder,true)
    }

    override fun onResourceReady(resource: Drawable,transition: Transition<in Drawable>?) {
      drawableTarget.get()?.setDrawable(resource,true)
    }

  }

}