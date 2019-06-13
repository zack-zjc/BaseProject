package com.base.sdk.http.handler

/**
 * author:zack
 * Date:2019/3/25
 * Description:返回数据处理类
 */
interface ErrorHandler {

  /**
   * 处理返回错误的handler
   * errorCode:错误码
   * errorMessage：错误信息
   */
  fun handleError(errorCode:Int,errorMessage:Any?): Boolean

}