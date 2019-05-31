package com.base.sdk.weex.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.base.sdk.weex.R
import com.base.sdk.weex.constants.WeexConstant
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.common.WXRenderStrategy
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment
import java.io.File

/**
 * author:zack
 * Date:2019/3/27
 * Description:展示weex的fragment
 */
open class WeexPageFragment : SwipeBackFragment() , IWXRenderListener {

  //instance对象
  protected var mWXSDKInstance: WXSDKInstance? = null

  //默认展示weex页面的layout
  protected var container: FrameLayout? = null

  /**
   * 是否lazyInit初始化界面
   */
  protected open fun lazyInitView() = true

  /**
   * weex地址
   */
  protected open fun weexUrl():String = arguments?.getString(WeexConstant.WEEX_URL)?:""

  /**
   * weex的界面的名字
   */
  protected open fun weexPageName():String = arguments?.getString(WeexConstant.WEEX_PAGE_NAME)?:""

  /**
   * 初始化
   */
  override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
    val view = LayoutInflater.from(context).inflate(R.layout.layout_weex_content_page_view,container,false)
    this.container = view.findViewById(R.id.id_weex_page_container)
    mWXSDKInstance = WXSDKInstance(context)
    mWXSDKInstance?.registerRenderListener(this)
    if (!lazyInitView()){
      initPage()
    }
    return view
  }

  /**
   * lazyInit
   */
  override fun onLazyInitView(savedInstanceState: Bundle?) {
    super.onLazyInitView(savedInstanceState)
    if (lazyInitView()){
      initPage()
    }
  }

  /**
   * 初始化加载页面
   */
  open fun initPage(){
    val renderUrl = weexUrl()
    val pageName = weexPageName()
    try {
      var url = renderUrl
      if (File(renderUrl).exists()){
        url = Uri.fromFile(File(renderUrl)).toString()
      }
      val renderOptions = mapOf(Pair(WXSDKInstance.BUNDLE_URL,url))
      mWXSDKInstance?.renderByUrl(pageName, url,renderOptions,null,WXRenderStrategy.APPEND_ASYNC)
    }catch (e:Exception){
      e.printStackTrace()
    }
  }


  /**
   * 刷新界面成功
   */
  override fun onRefreshSuccess(instance: WXSDKInstance?,width: Int,height: Int) {

  }

  /**
   * 加载界面失败
   */
  override fun onException(instance: WXSDKInstance?,errCode: String?,msg: String?) {
    Log.e("ActWeexPage",msg.toString())
  }

  /**
   * 加载界面出现
   */
  override fun onViewCreated(instance: WXSDKInstance?,view: View?) {
    view?.let {
      if (it.parent != null){
        (it.parent as ViewGroup).removeView(it)
      }
      container?.addView(view)
    }
    try{
      mWXSDKInstance?.onActivityCreate()
      mWXSDKInstance?.onActivityStart()
      mWXSDKInstance?.onActivityResume()
    }catch (e:Exception){
      e.printStackTrace()
    }
  }

  /**
   * 加载成功
   */
  override fun onRenderSuccess(instance: WXSDKInstance?,width: Int,height: Int) {

  }

  /**
   * 生命周期处理
   */
  override fun onStart() {
    super.onStart()
    mWXSDKInstance?.onActivityStart()
  }

  /**
   * 生命周期处理
   */
  override fun onResume() {
    super.onResume()
    mWXSDKInstance?.onActivityResume()
  }

  /**
   * 生命周期处理
   */
  override fun onPause() {
    super.onPause()
    mWXSDKInstance?.onActivityPause()
  }

  /**
   * 生命周期处理
   */
  override fun onStop() {
    super.onStop()
    mWXSDKInstance?.onActivityStop()
  }

  /**
   * 生命周期处理
   */
  override fun onDestroyView() {
    super.onDestroyView()
    mWXSDKInstance?.onActivityDestroy()
  }

  /**
   * 生命周期处理
   */
  override fun onBackPressedSupport(): Boolean {
    if (mWXSDKInstance?.onActivityBack() != true) {
      return super.onBackPressedSupport()
    }
    return true
  }

  /**
   * 处理返回事件
   */
  override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    mWXSDKInstance?.onActivityResult(requestCode, resultCode, data)
  }

}