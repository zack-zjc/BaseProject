package com.base.sdk.glide

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.DataFetcher.DataCallback
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Preconditions
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream

/**
 * author:zack
 * Date:2019/2/28
 * Description:null
 */
class OkHttpStreamFetcher constructor(private val client :Call.Factory, private val url :GlideUrl) : DataFetcher<InputStream>,okhttp3.Callback{

  private var stream: InputStream? = null
  private var responseBody: ResponseBody? = null
  private var callback: DataCallback<in InputStream>? = null
  // call may be accessed on the main thread while the object is in use on other threads. All other
  // accesses to variables may occur on different threads, but only one at a time.
  @Volatile private var call: Call? = null

  override fun loadData(priority: Priority,callback: DataCallback<in InputStream>) {
    val requestBuilder = Request.Builder().url(url.toStringUrl())
    for (headerEntry in url.headers.entries) {
      val key = headerEntry.key
      requestBuilder.addHeader(key, headerEntry.value)
    }
    val request = requestBuilder.build()
    this.callback = callback
    this.call = client.newCall(request)
    this.call?.enqueue(this)
  }

  override fun onFailure(call: Call,e: IOException) {
    callback?.onLoadFailed(e)
  }

  override fun onResponse(call: Call,response: Response) {
    responseBody = response.body()
    if (response.isSuccessful) {
      val contentLength = Preconditions.checkNotNull(responseBody).contentLength()
      stream = ContentLengthInputStream.obtain(responseBody?.byteStream()!!, contentLength)
      callback?.onDataReady(stream)
    } else {
      callback?.onLoadFailed(HttpException(response.message(), response.code()))
    }
  }

  override fun cleanup() {
    try {
      stream?.close()
    } catch (e: IOException) {
      e.printStackTrace()
    }
    responseBody?.close()
    callback = null
  }

  override fun cancel() {
    call?.cancel()
  }

  override fun getDataClass(): Class<InputStream> = InputStream::class.java

  override fun getDataSource(): DataSource = DataSource.REMOTE

}