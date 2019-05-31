package com.base.sdk.web.config

import android.graphics.Color

/**
 * author:zack
 * Date:2019/3/29
 * Description:web的设置
 */
class WebSetting {

  //网址host白名单
  var whiteHost :List<String> = arrayListOf()

  //UA
  lateinit var userAgent:String

  //js的名称
  lateinit var jsInterfaceName:String

  //toolbar的颜色
  var toolBarColor :Int = Color.TRANSPARENT

  //是否是深色的toolbar
  var isDarkToolbar:Boolean = false

  class Builder(uA:String,jsName:String) {

    private var whiteList :ArrayList<String> = arrayListOf()

    private var userAgent:String = uA

    private var jsInterfaceName:String = jsName

    private var toolBarColor :Int = Color.TRANSPARENT

    private var isDarkToolbar:Boolean = false

    /**
     * 添加白名单host
     */
    fun addWhiteHost(whiteList :List<String>):Builder{
      this.whiteList.addAll(whiteList)
      return this
    }

    /**
     * 设置toolbar颜色
     */
    fun toolBarColor(toolBarColor :Int):Builder{
      this.toolBarColor = toolBarColor
      return this
    }

    /**
     * 设置是否是深色的toolbar
     */
    fun isDarkToolbar(isDarkBar:Boolean):Builder{
      this.isDarkToolbar = isDarkBar
      return this
    }

    /**
     * 创建WebSetting
     */
    fun build():WebSetting{
      val webSetting = WebSetting()
      webSetting.whiteHost = whiteList
      webSetting.jsInterfaceName = jsInterfaceName
      webSetting.userAgent = userAgent
      webSetting.isDarkToolbar = isDarkToolbar
      webSetting.toolBarColor = toolBarColor
      return webSetting
    }

  }


}