package com.base.sdk.web.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.base.sdk.base.activity.BasePermissionActivity
import com.base.sdk.share.ShareUtil
import com.base.sdk.web.R
import com.base.sdk.web.R.layout
import com.base.sdk.web.R.mipmap
import com.base.sdk.web.config.WebCustomSetting
import com.base.sdk.web.constant.WebConstants
import com.base.sdk.web.entity.ShareEntity
import com.base.sdk.web.jsinterface.JScriptInterface
import com.base.sdk.web.ui.chrome.BaseWebChromeClient
import com.base.sdk.web.ui.chrome.VideoChromeInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File

/**
 * author:zack
 * Date:2019/3/28
 * Description:webView的基类
 */
abstract class ActBaseWebView : BasePermissionActivity(),OnClickListener{

  //请求扫描
  val REQUEST_SCAN = 55

  //是否展示分享
  protected var showShare = true

  //分享的数据
  protected var shareEntity: ShareEntity? = null

  //扫码的callback返回
  protected var scanCllback:String? =null

  //webview对象
  protected var mWebView:WebView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(getPageLayoutId())
    val pageName = intent.getStringExtra(WebConstants.PAGE_NAME) ?:""
    val showShare = intent.getBooleanExtra(WebConstants.SHOW_SHARE,true)
    val showMore = intent.getBooleanExtra(WebConstants.SHOW_MORE,true)
    initToolbar(pageName,showShare,showMore)
    configWebView()
    getWebViewContainer().addView(getWebView(),ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    val url = intent.getStringExtra(WebConstants.INTENT_URL)
    if (WebCustomSetting.checkWhiteHost()){
      if (WebCustomSetting.isInWhiteList(url)){
        getWebView()?.loadUrl(url)
      }
    }else{
      getWebView()?.loadUrl(url)
    }
  }

  override fun onResume() {
    super.onResume()
    getWebView()?.onResume()
  }

  override fun onPause() {
    super.onPause()
    getWebView()?.onPause()
  }

  /**
   * 回退
   */
  override fun onBackPressedSupport() {
    if (getWebView()?.canGoBack() == true){
      getWebView()?.goBack()
    }else{
      super.onBackPressedSupport()
    }
  }

  override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK){
      if (requestCode == REQUEST_SCAN){ //处理扫码返回
        val qrCode = data?.getStringExtra("qrCode")
        scanCllback?.let {
          if (!qrCode.isNullOrEmpty()){
            getWebView()?.loadUrl("javascript:$it('$qrCode');")
          }
        }
      }
    }
  }

  //chromeClient
  private val mWebChromeClient = object : BaseWebChromeClient(VideoChromeInterface(this,getWebView())) {

    override fun onProgressChanged(view: WebView?,newProgress: Int) {
      super.onProgressChanged(view, newProgress)
      initProgress(newProgress)
    }

    override fun onReceivedTitle(view: WebView?,title: String?) {
      super.onReceivedTitle(view, title)
      title?.let {
        initTitle(it)
      }
    }
  }

  private val mWebViewClient = object:WebViewClient(){

    override fun shouldOverrideUrlLoading(view: WebView?,request: WebResourceRequest?): Boolean {
      if(request == null) return false
      val url = request.url.toString()
      if (url.startsWith("http") || url.startsWith("https")){
        view?.loadUrl(url)
      }else{
        try {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
          view?.context?.startActivity(intent)
        }catch (e:Exception){
          e.printStackTrace()
        }
      }
      return true
    }
  }

  private val downloadListenr = DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
    try {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      this@ActBaseWebView.startActivity(intent)
    }catch (e:Exception){
      e.printStackTrace()
    }
  }

  /**
   * 配置webview
   */
  open fun configWebView(){
    mWebView = WebView(this)
    val mWebSettings = getWebView()?.settings
    mWebSettings?.let {
      it.javaScriptEnabled = getJsInterface() != null
      it.setSupportZoom(true)
      it.builtInZoomControls = false
      it.savePassword = false
      //适配5.0不允许http和https混合使用情况
      it.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      getWebView()?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
      it.textZoom = 100
      it.databaseEnabled = true
      it.loadsImagesAutomatically = true
      it.setSupportMultipleWindows(false)
      // 是否阻塞加载网络图片  协议http or https
      it.blockNetworkImage = false
      // 允许加载本地文件html  file协议
      it.allowFileAccess = true
      // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
      it.allowFileAccessFromFileURLs = true
      // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
      it.allowUniversalAccessFromFileURLs = true
      it.javaScriptCanOpenWindowsAutomatically = true
      it.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
      it.loadWithOverviewMode = false
      it.useWideViewPort = false
      it.domStorageEnabled = true
      it.setNeedInitialFocus(true)
      it.defaultTextEncodingName = "utf-8"//设置编码格式
      it.defaultFontSize = 16
      it.minimumFontSize = 12//设置 WebView 支持的最小字体大小，默认为 8
      it.setGeolocationEnabled(true)
      it.userAgentString = "${it.userAgentString} ${WebCustomSetting.getUserAgent()}"
    }
    getWebView()?.webChromeClient = getWebChromeClient()
    getWebView()?.webViewClient = getWebViewClient()
    getWebView()?.setDownloadListener(getDownloadListener())
    getWebView()?.addJavascriptInterface(getJsInterface(),WebCustomSetting.getJsInterfaceName())
  }

  /**
   * 获取webview对象
   */
  open fun getWebView():WebView? = mWebView

  /**webSetting
   * 获取chromeClient
   */
  open fun getWebChromeClient():WebChromeClient = mWebChromeClient

  /**
   * 获取webClient
   */
  open fun getWebViewClient():WebViewClient = mWebViewClient

  /**
   * 获取下载管理器
   */
  open fun getDownloadListener():DownloadListener = downloadListenr

  /**
   * 点击事件处理
   */
  override fun onClick(view: View?) {
    when {
      view?.id == R.id.id_web_close_view -> finish()
      view?.id == R.id.id_web_back_view -> onBackPressedSupport()
      view?.id == R.id.id_web_menu_icon -> moreMenuClick(view)
    }
  }

  /**
   * 获取页面样式
   */
  open fun getPageLayoutId() = layout.layout_web_main_view

  /**
   * 获取装载webview的容器
   */
  open fun getWebViewContainer():ViewGroup = findViewById(R.id.id_web_web_view)

  open fun initTitle(pageName:String){
    val title = findViewById<TextView>(R.id.id_web_title_view)
    title.text = pageName
  }

  /**
   * 初始化toolbar
   */
  private fun initToolbar(pageName:String,canShare:Boolean,showMore:Boolean){
    val title = findViewById<TextView>(R.id.id_web_title_view)
    title.text = pageName
    val backView = findViewById<ImageView>(R.id.id_web_back_view)
    backView.setOnClickListener(this)
    val menuView = findViewById<ImageView>(R.id.id_web_menu_icon)
    menuView.setOnClickListener(this)
    val closeView = findViewById<TextView>(R.id.id_web_close_view)
    closeView.setOnClickListener(this)
    if (WebCustomSetting.isUseDarkToolbar()){
      backView.setImageResource(mipmap.icon_web_navigation_white_back)
      menuView.setImageResource(mipmap.icon_web_more_white_setting)
      title.setTextColor(Color.WHITE)
      closeView.setTextColor(Color.WHITE)
    }
    if (!showMore){
      findViewById<View>(R.id.id_web_menu_icon).visibility = View.GONE
    }else{
      showShare = canShare
    }
  }

  /**
   * 设置进度
   */
  open fun initProgress(percent:Int){
    showProgress(percent < 100)
    val progressBar = findViewById<ProgressBar>(R.id.id_web_progressbar)
    progressBar.progress = percent
  }

  /**
   * 显示或隐藏进度条
   */
  open fun showProgress(show:Boolean){
    val progressBar = findViewById<ProgressBar>(R.id.id_web_progressbar)
    val state = if (show) View.VISIBLE else View.INVISIBLE
    if (progressBar.visibility != state){
      progressBar.visibility = state
    }
  }

  /**
   * 设置分享数据
   */
  open fun setShareData(title:String,desc:String,image:String,html:String){
    shareEntity = ShareEntity(title,desc,"",html)
    Glide.with(this).asFile().load(image).into(object : SimpleTarget<File>(){
      override fun onResourceReady(resource: File,transition: Transition<in File>?) {
        shareEntity = ShareEntity(title,desc,resource.absolutePath,html)
      }
    })
  }

  /**
   *  默认分享操作
   */
  open fun shareDefault(){
    ShareUtil.share(this,shareEntity?.title,shareEntity?.message,shareEntity?.imagePath,shareEntity?.htmlPath)
  }

  /**
   * 开启扫码
   */
  fun startScan(callbackName: String){
    this.scanCllback = callbackName
    Toast.makeText(this,"未实现startScan",Toast.LENGTH_SHORT).show()
  }

  /**
   * 获取jsInterface
   */
  abstract fun getJsInterface(): JScriptInterface?

  /**
   * 更多按钮点击事件
   */
  abstract fun moreMenuClick(view: View)

}