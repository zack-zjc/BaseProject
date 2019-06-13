package com.base.sdk.http.handler.impl

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.base.sdk.http.ApplicationContext
import com.base.sdk.http.HttpConstants
import com.base.sdk.http.R
import com.base.sdk.http.handler.ErrorHandler

/**
 * author:zack
 * Date:2019/3/25
 * Description:默认的结果处理类
 */
open class DefaultErrorHandler : ErrorHandler {

  private var mHandler = Handler(Looper.getMainLooper())

   override fun handleError(errorCode:Int,errorMessage:Any?):Boolean {
    when(errorCode){
      HttpConstants.HTTP_ERROR_UNKNOWN ->{
        val message = if (errorMessage.toString() != "null") errorMessage
        else ApplicationContext.CONTEXT.resources.getString(R.string.network_unknown_error)
        showToast(message.toString())
        return true
      }
      HttpConstants.HTTP_ERROR_NETWORK ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_network_error))
        return true
      }
      HttpConstants.HTTP_ERROR_SSL_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_ssl_error))
        return true
      }
      HttpConstants.HTTP_ERROR_IO_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_io_error))
        return true
      }
      HttpConstants.HTTP_ERROR_SSL_PEER_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_ssl_peer_error))
        return true
      }
      HttpConstants.HTTP_ERROR_SSL_HAND_SHAKE_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_ssl_hand_shake_error))
        return true
      }
      HttpConstants.HTTP_ERROR_FILE_NOT_FOUND_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_file_not_found_error))
        return true
      }
      HttpConstants.HTTP_ERROR_UNKNOWN_HOST_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_unknown_host_error))
        return true
      }
      HttpConstants.HTTP_ERROR_CONNECT_TIMEOUT_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_connect_timeout_error))
        return true
      }
      HttpConstants.HTTP_ERROR_SOCKET_TIMEOUT_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_socket_timeout_error))
        return true
      }
      HttpConstants.HTTP_ERROR_WHITE_LIST_EXCEPTION ->{
        showToast(ApplicationContext.CONTEXT.resources.getString(R.string.network_whitelist_error))
        return true
      }
    }
    return false
  }

  /**
   * 显示提示
   */
  fun showToast(message:String){
    mHandler.post { Toast.makeText(ApplicationContext.CONTEXT,message,Toast.LENGTH_SHORT).show() }
  }

}