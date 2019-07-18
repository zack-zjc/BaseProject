package com.base.sdk.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import okhttp3.Call
import java.io.InputStream

/**
 * author:zack
 * Date:2019/2/28
 * Description:null
 */
class OkHttpUrlLoader(clientFactory: Call.Factory) : ModelLoader<GlideUrl, InputStream>{

  private val client: Call.Factory = clientFactory

  override fun handles(model: GlideUrl): Boolean = true

  override fun buildLoadData(model: GlideUrl,width: Int,height: Int,options: Options): LoadData<InputStream>? {
    return LoadData<InputStream>(model, OkHttpStreamFetcher(client, model))
  }

  /**
   * The default factory for [OkHttpUrlLoader]s.
   */
  class Factory (private val client: Call.Factory) : ModelLoaderFactory<GlideUrl, InputStream> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
      return OkHttpUrlLoader(client)
    }

    override fun teardown() {
      // Do nothing, this instance doesn't own the client.
    }
  }

}