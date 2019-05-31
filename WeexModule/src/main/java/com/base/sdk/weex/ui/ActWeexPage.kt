package com.base.sdk.weex.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.base.sdk.weex.R
import com.base.sdk.weex.constants.WeexConstant
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.bridge.JSCallback
import com.taobao.weex.common.WXRenderStrategy
import java.io.File

open class ActWeexPage : ActBaseWeexPage() ,OnClickListener, IWXRenderListener {

  //默认展示weex页面的layout
  protected var container:FrameLayout? = null

  //返回按钮
  protected var backIcon:ImageView? = null

  //页面标题
  protected var title:TextView? = null

  //菜单的文字
  protected var menuText:TextView? = null

  //自定义菜单图标
  protected var menuCustomIcon:ImageView? = null

  //自定义的导航栏
  protected var toolBar:View? = null

  //menu文字的callback
  protected var menuTextCallback:JSCallback? = null

  //menu图标的callback
  protected var menuIconCustomCallback:JSCallback? = null

  //instance对象
  protected var mWXSDKInstance: WXSDKInstance? = null

  //是否渲染成功
  private var renderSucess = false

  /**
   * 根布局的resId
   * 默认-1为使用默认根布局
   */
  protected fun getLayoutId() = R.layout.layout_weex_main_page


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(getLayoutId())
    if (getLayoutId() == R.layout.layout_weex_main_page){
      container = findViewById(R.id.id_weex_page_container)
      toolBar = findViewById(R.id.id_weex_title_container)
      backIcon = findViewById(R.id.id_weex_back_icon)
      backIcon?.setOnClickListener(this)
      title = findViewById(R.id.id_weex_title)
      menuText = findViewById(R.id.id_weex_menu_text)
      menuText?.setOnClickListener(this)
      menuCustomIcon = findViewById(R.id.id_weex_menu_custom_icon)
      menuCustomIcon?.setOnClickListener(this)
    }
    val renderUrl = intent.getStringExtra(WeexConstant.WEEX_URL)
    val pageName = intent.getStringExtra(WeexConstant.WEEX_PAGE_NAME)
    mWXSDKInstance = WXSDKInstance(this)
    mWXSDKInstance?.registerRenderListener(this)
    initPage(renderUrl,pageName)
  }

  /**
   * 初始化加载页面
   */
  open fun initPage(renderUrl:String?,pageName:String?){
    try {
      renderUrl?.let {
        var url = it
        if (File(it).exists()){
            url = Uri.fromFile(File(it)).toString()
        }
        val renderOptions = mapOf(Pair(WXSDKInstance.BUNDLE_URL,url))
        mWXSDKInstance?.renderByUrl(pageName, url,renderOptions,null,
            WXRenderStrategy.APPEND_ASYNC)
      }
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
   * 返回处理
   */
  override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    mWXSDKInstance?.onActivityResult(requestCode, resultCode, data)
  }

  /**
   * 界面返回处理
   */
  override fun onBackPressedSupport() {
    if (mWXSDKInstance?.onActivityBack() == false) {
      super.onBackPressedSupport()
    }
  }

  /**
   * 加载界面出现
   */
  override fun onViewCreated(instance: WXSDKInstance?,view: View?) {
    view?.let {
      if (it.parent != null){
        (it.parent as ViewGroup).removeView(it)
      }
      container?.removeAllViews()
      container?.addView(view)
    }
    try{
      renderSucess = true
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
   * 设置status的状态
   * statusBarColor自定义状态栏的背景颜色
   * isDarkStatus是否使用深色的bar,true使用白色图标，false:黑色图标
   */
  open fun setStatusTheme(statusBarColor:Int,isDarkStatus:Boolean){
    initStatusBarColor(statusBarColor)
    toolBar?.setBackgroundColor(statusBarColor)
    setPageStyle(isDarkStatus)
  }

  /**
   * 设置界面是否是白色系图标
   * whiteStyle:true白色系图标,false otherwise
   */
  open fun setPageStyle(whiteStyle:Boolean){
    backIcon?.setImageResource(if (whiteStyle) R.mipmap.icon_weex_navigation_white_back else R.mipmap.icon_weex_navigation_back)
    title?.setTextColor(if (whiteStyle) resources.getColor(
        R.color.color_weex_title_white_color
    )
        else resources.getColor(R.color.color_weex_title_color))
    menuText?.setTextColor(if (whiteStyle) resources.getColor(
        R.color.color_weex_menu_white_color
    )
    else resources.getColor(R.color.color_weex_menu_color))
  }

  /**
   * 设置界面标题
   */
  open fun setPageTitle(titleStr:String){
    title?.text = titleStr
  }

  /**
   * 隐藏导航栏
   */
  open fun hideToolbar(hide:Boolean){
    toolBar?.visibility = if (hide) View.GONE else View.VISIBLE
  }

  /**
   * 设置menu的文字菜单
   */
  open fun setMenuText(menuStr:String,callback: JSCallback){
    menuText?.visibility = View.VISIBLE
    menuText?.text = menuStr
    menuTextCallback = callback
  }

  /**
   * 设置menu的图标菜单
   */
  open fun setMenuIcon(menuUrl:String,callback: JSCallback){
    menuCustomIcon?.visibility = View.VISIBLE
    menuCustomIcon?.let {
      Glide.with(this).asDrawable().load(menuUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(it)
    }
    menuIconCustomCallback = callback
  }

  override fun onClick(view: View?) {
    view?.let {
      when(it.id){
        backIcon?.id ->{
          onBackPressedSupport()
        }
        menuText?.id ->{
          menuTextCallback?.invokeAndKeepAlive(0)
        }
        menuCustomIcon?.id ->{
          menuIconCustomCallback?.invokeAndKeepAlive(0)
        }
        else -> {

        }
      }
    }
  }

  /**
   * 生命周期处理
   */
  override fun onStart() {
    super.onStart()
    if(renderSucess){
      mWXSDKInstance?.onActivityStart()
    }
  }

  /**
   * 生命周期处理
   */
  override fun onResume() {
    super.onResume()
    if(renderSucess){
      mWXSDKInstance?.onActivityResume()
    }
  }

  /**
   * 生命周期处理
   */
  override fun onPause() {
    super.onPause()
    if(renderSucess){
      mWXSDKInstance?.onActivityPause()
    }
  }

  /**
   * 生命周期处理
   */
  override fun onStop() {
    super.onStop()
    if(renderSucess){
      mWXSDKInstance?.onActivityStop()
    }
  }

  /**
   * 生命周期处理
   */
  override fun onDestroy() {
    super.onDestroy()
    mWXSDKInstance?.onActivityDestroy()
  }

}
