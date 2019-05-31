package com.base.sdk.web.pop

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.base.sdk.web.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * author:zack
 * Date:2019/3/27
 * Description:itemAdapter
 */
class PopAdapter(private val context:Context,private val menus:List<MenuBean>,private val dialog: PopupWindow) : BaseAdapter(),OnClickListener {

  @SuppressLint("ViewHolder")
  override fun getView(position: Int,convertView: View?,parent: ViewGroup?): View {
    val view =  LayoutInflater.from(context).inflate(R.layout.layout_base_pop_window_item,parent,false)
    val textView = view.findViewById<TextView>(R.id.id_base_pop_item_text)
    val imageView = view.findViewById<ImageView>(R.id.id_base_pop_item_image)
    val menu = menus[position]
    textView.text = menu.menuText
    when {
      menu.iconId != 0 -> imageView.setImageResource(menu.iconId)
      menu.iconUrl.isNotEmpty() -> Glide.with(view).asDrawable().load(menu.iconUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
      else -> imageView.visibility = View.GONE
    }
    view.setOnClickListener(this)
    view.tag = menu
    return view
  }

  override fun getItem(position: Int): Any {
   return menus[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getCount(): Int {
    return menus.size
  }

  override fun onClick(view: View?) {
    val menuBean = view?.tag as MenuBean
    menuBean.clickListener.onClick(view)
    dialog.dismiss()
  }

}