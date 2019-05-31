package com.base.sdk.http

/**
 * author:zack
 * Date:2019/3/25
 * Description:网络请求的常量
 */
object HttpConstants {

  //未知错误
  const val HTTP_ERROR_UNKNOWN = -1000

  //网络未连接错误
  const val HTTP_ERROR_NETWORK = -1001

  //认证失败
  const val HTTP_ERROR_SSL_EXCEPTION = -1002

  //io读取错误
  const val HTTP_ERROR_IO_EXCEPTION = -1003

  //ssl错误，认证名称失败
  const val HTTP_ERROR_SSL_PEER_EXCEPTION = -2000

  //服务器证书认证失败
  const val HTTP_ERROR_SSL_HAND_SHAKE_EXCEPTION = -2001

  //文件未找到
  const val HTTP_ERROR_FILE_NOT_FOUND_EXCEPTION = -2002

  //位置host错误
  const val HTTP_ERROR_UNKNOWN_HOST_EXCEPTION = -2003

  //连接超时
  const val HTTP_ERROR_CONNECT_TIMEOUT_EXCEPTION = -2004

  //socket读取超时
  const val HTTP_ERROR_SOCKET_TIMEOUT_EXCEPTION = -2005

  //地址白名单错误
  const val HTTP_ERROR_WHITE_LIST_EXCEPTION = -3000





}
