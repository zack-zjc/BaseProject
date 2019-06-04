package com.base.sdk.web.pop

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.PopupWindow
import com.base.sdk.web.R

/**
 * author:zack
 * Date:2019/3/27
 * Description:气泡弹窗
 */
object PopWindowUtil {


  /**
   * 展示menu弹框
   * rootView：点击展示弹框的view
   * menus：菜单列表
   */
  fun showPopWindow(rootView:View,menus:List<MenuBean>){
    val popupWindow = PopupWindow(rootView.context)
    //获取当前Activity的Window的decorView作为测量的父布局
    val activity = rootView.context as Activity
    val decorView = activity.window.decorView as FrameLayout
    //填充获得自己的popWindow的contentView
    val contentView = LayoutInflater.from(rootView.context).inflate(R.layout.layout_base_pop_window_view, decorView, false)
    val listView = contentView.findViewById<ListView>(R.id.id_base_pop_list)
    listView.adapter = PopAdapter(rootView.context,menus,popupWindow)
    //构造父类的withSpec和heightSpec
    val withSpec = View.MeasureSpec.makeMeasureSpec(decorView.measuredWidth, View.MeasureSpec.AT_MOST)
    val heightSpec = View.MeasureSpec.makeMeasureSpec(decorView.measuredHeight, View.MeasureSpec.AT_MOST)
    //真正的提前计算内容高度
    contentView.measure(withSpec, heightSpec)
    //设置PopWindow的contentView的计算得出的实际高度
    popupWindow.animationStyle = R.style.style_base_pop_window
    popupWindow.isOutsideTouchable = true
    popupWindow.isFocusable = true
    popupWindow.isTouchable = true
    popupWindow.setBackgroundDrawable(ColorDrawable())
    popupWindow.width = dpToPixel(rootView.context,150)
    popupWindow.height = contentView.measuredHeight
    popupWindow.contentView = contentView
    if (!popupWindow.isShowing) {
      popupWindow.showAsDropDown(rootView,rootView.measuredWidth-dpToPixel(rootView.context,150),10)
    }
  }

  /**
   * 转化dp为pixel
   */
  private fun dpToPixel(context:Context,dp: Int): Int {
    val displayMetrics = context.applicationContext.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).toInt()
  }

}