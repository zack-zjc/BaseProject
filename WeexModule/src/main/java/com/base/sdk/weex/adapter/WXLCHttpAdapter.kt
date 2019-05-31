package com.base.sdk.weex.adapter

import android.text.TextUtils
import com.taobao.weex.WXSDKManager
import com.taobao.weex.adapter.DefaultWXHttpAdapter
import com.taobao.weex.adapter.IWXHttpAdapter
import com.taobao.weex.adapter.IWXHttpAdapter.OnHttpListener
import com.taobao.weex.common.WXRequest
import com.taobao.weex.common.WXResponse
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * author:zack
 * Date:2019/4/15
 * Description:网络请求的adapter
 * 继承默认的实现，只为修改header
 */
class WXLCHttpAdapter : DefaultWXHttpAdapter() {

  /**
   * 自定义处理header
   * 由于默认的DefaultWXHttpAdapter会导致当前的header如果存在多个List<String>则会默认取第一个使用的问题所以重写
   */
  private fun resolvedHeader(current :Map<String,List<String>>) : Map<String,List<String>>{
    val result = mutableMapOf<String,List<String>>()
    for ((key,value) in current){
      if (value.size <= 1){
        result[key] = value
      }else{
        var tempStr = ""
        for (str in value){
          tempStr +=str
        }
        result[key] = arrayListOf(tempStr)
      }
    }
    return result
  }

  private var mCustomExecutorService: ExecutorService? = null

  private fun execute(runnable: Runnable) {
    if (mCustomExecutorService == null) {
      mCustomExecutorService = Executors.newFixedThreadPool(3)
    }
    mCustomExecutorService?.execute(runnable)
  }

  override fun sendRequest(request: WXRequest?,listener: OnHttpListener?) {
    listener?.onHttpStart()
    execute(Runnable {
      val instance = WXSDKManager.getInstance().allInstanceMap[request?.instanceId]
      if (null != instance && !instance.isDestroy) {
        instance.apmForInstance.actionNetRequest()
      }
      var isNetRequestSucceed = true
      val response = WXResponse()
      val reporter = eventReporterDelegate
      try {
        val connection = openConnection(request!!, listener)
        reporter.preConnect(connection, request.body)
        val headers = resolvedHeader(connection.headerFields)
        val responseCode = connection.responseCode
        listener?.onHeadersReceived(responseCode, headers)
        reporter.postConnect()
        response.statusCode = responseCode.toString()
        if (responseCode in 200..299) {
          var rawStream = connection.inputStream
          rawStream = reporter.interpretResponseStream(rawStream)
          response.originalData = readInputStreamAsBytes(rawStream, listener)
        } else {
          response.errorMsg = readInputStream(connection.errorStream, listener)
          isNetRequestSucceed = false
        }
        listener?.onHttpFinish(response)
      } catch (e: IOException) {
        isNetRequestSucceed = false
        e.printStackTrace()
        response.statusCode = "-1"
        response.errorCode = "-1"
        response.errorMsg = e.message
        listener?.onHttpFinish(response)
        try {
          reporter.httpExchangeFailed(e)
        } catch (t: Throwable) {
          t.printStackTrace()
        }
      } catch (e: IllegalArgumentException) {
        isNetRequestSucceed = false
        e.printStackTrace()
        response.statusCode = "-1"
        response.errorCode = "-1"
        response.errorMsg = e.message
        listener?.onHttpFinish(response)
        if (e is IOException) {
          try {
            reporter.httpExchangeFailed(e as IOException)
          } catch (t: Throwable) {
            t.printStackTrace()
          }

        }
      }
      if (null != instance && !instance.isDestroy) {
        instance.apmForInstance.actionNetResult(isNetRequestSucceed, null)
      }
    })
  }

  /**
   * Opens an [HttpURLConnection] with parameters.
   * @param request
   * @param listener
   * @return an open connection
   * @throws IOException
   */
  @Throws(IOException::class)
  private fun openConnection(request: WXRequest,listener: IWXHttpAdapter.OnHttpListener?): HttpURLConnection {
    val url = URL(request.url)
    val connection = createConnection(url)
    connection.connectTimeout = request.timeoutMs
    connection.readTimeout = request.timeoutMs
    connection.useCaches = false
    connection.doInput = true
    if (request.paramMap != null) {
      val keySets = request.paramMap.keys
      for (key in keySets) {
        connection.addRequestProperty(key, request.paramMap[key])
      }
    }
    if ("POST" == request.method || "PUT" == request.method || "PATCH" == request.method) {
      connection.requestMethod = request.method
      if (request.body != null) {
        listener?.onHttpUploadProgress(0)
        connection.doOutput = true
        val out = DataOutputStream(connection.outputStream)
        out.write(request.body.toByteArray())
        out.close()
        listener?.onHttpUploadProgress(100)
      }
    } else if (!TextUtils.isEmpty(request.method)) {
      connection.requestMethod = request.method
    } else {
      connection.requestMethod = "GET"
    }

    return connection
  }

  @Throws(IOException::class)
  private fun readInputStreamAsBytes(inputStream: InputStream?,listener: IWXHttpAdapter.OnHttpListener?): ByteArray? {
    if (inputStream == null) {
      return null
    }
    val buffer = ByteArrayOutputStream()
    var readCount = 0
    val data = ByteArray(2048)
    var nRead = inputStream.read(data, 0, data.size)
    while (nRead != -1) {
      buffer.write(data, 0, nRead)
      readCount += nRead
      listener?.onHttpResponseProgress(readCount)
      nRead = inputStream.read(data, 0, data.size)
    }
    buffer.flush()
    return buffer.toByteArray()
  }

  @Throws(IOException::class)
  private fun readInputStream(inputStream: InputStream?,listener: IWXHttpAdapter.OnHttpListener?): String? {
    if (inputStream == null) {
      return null
    }
    val builder = StringBuilder()
    val localBufferedReader = BufferedReader(InputStreamReader(inputStream))
    val data = CharArray(2048)
    var len = localBufferedReader.read(data)
    while (len != -1) {
      builder.append(data, 0, len)
      listener?.onHttpResponseProgress(builder.length)
      len = localBufferedReader.read(data)
    }
    localBufferedReader.close()
    return builder.toString()
  }

}