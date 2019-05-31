package com.base.sdk.base.activity

import android.content.pm.PackageManager
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import com.base.sdk.base.util.PermissionDialogUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable

/**
 * author:zack
 * Date:2019/3/1
 * Description:关于权限的页面
 * 包含权限申报和获取权限操作
 */
abstract class BasePermissionActivity : BaseActivity() {

  private var firstRequestPermission = true

  private val rxPermissions by lazy { RxPermissions(this) }

  private var disposable : Disposable? = null

  private var customDisposable : Disposable? = null

  override fun onStart() {
    super.onStart()
    if (firstRequestPermission && !requestedPermission().isEmpty()){
      firstRequestPermission = false
      requestPermissions()
    }
  }

  /**
   * 跳转展示说明弹框
   */
  protected open fun requestPermissions(){
    if (checkAllPermission(requestedPermission())){
      gainPermissionAfterAction()
    }else{
      PermissionDialogUtil.jumpPermissionExplainDialog(this,permissionExplainTitle(),permissionExplainDesc(),
          this::refusePermissionAction) {
        disposable = rxPermissions.requestEachCombined(*requestedPermission()).subscribe {
          when{
            it.granted -> gainPermissionAfterAction()//全部同意后调用
            it.shouldShowRequestPermissionRationale -> forbidPermissionAfterAction()//只要有一个选择：禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
            else -> forbidForeverPermissionAfterAction()//只要有一个选择：禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
          }
        }
      }
    }
  }

  /**
   * 检测是否所有权限都通过了
   */
  private fun checkAllPermission(@NonNull permissions:Array<String>):Boolean{
    var result = true
    if (permissions.isNotEmpty()){
      for (permission in permissions){
        result = result and (ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED)
        if (!result) break
      }
    }
    return result
  }

  /**
   * 请求权限
   */
  fun requestCustomPermission(@NonNull permissions:Array<String>,title:CharSequence,desc:CharSequence,callback:(Boolean)->Unit){
    if (checkAllPermission(permissions)){
      callback(true)
    }else{
      PermissionDialogUtil.jumpPermissionExplainDialog(this,title,desc,{callback(false)}) {
        customDisposable = rxPermissions.requestEachCombined(*permissions).subscribe {
          when {
            it.granted -> callback(true) //全部同意后调用
            it.shouldShowRequestPermissionRationale -> callback(false) //只要有一个选择：禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
            else -> callback(false)//只要有一个选择：禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
          }
        }
      }
    }
  }

  /**
   * 获取到权限之后的操作
   */
  protected open fun gainPermissionAfterAction(){

  }

  /**
   * 普通禁止权限后的操作
   */
  protected open fun forbidPermissionAfterAction(){
    finish()
  }

  /**
   * 禁止权限以后不再询问后的操作
   */
  protected open fun forbidForeverPermissionAfterAction(){
    finish()
  }

  /**
   * 需要的权限
   */
  protected open fun requestedPermission():Array<String> = arrayOf()

  /**
   * 本地弹框权限申请的说明
   */
  protected open fun permissionExplainDesc():CharSequence = ""

  /**
   * 本地弹框权限申请的头部说明
   */
  protected open fun permissionExplainTitle():String = "温馨提示"

  /**
   * 本地弹框拒绝权限申请说明的处理
   */
  protected open fun refusePermissionAction(){
    finish()
  }

  override fun onDestroy() {
    disposable?.let {
      if (!it.isDisposed){
        it.dispose()
      }
    }
    customDisposable?.let {
      if (!it.isDisposed){
        it.dispose()
      }
    }
    super.onDestroy()
  }

}