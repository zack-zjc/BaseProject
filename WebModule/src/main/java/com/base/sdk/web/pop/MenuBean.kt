package com.base.sdk.web.pop

import android.view.View.OnClickListener

/**
 * author:zack
 * Date:2019/3/27
 * Description:menu对象
 * key：关键字
 * iconUrl：图标路径
 * menuText：菜单文字
 * clickListener：点击事件
 */
data class MenuBean (val iconId:Int=0,val iconUrl:String="",val menuText:String,val clickListener: OnClickListener)