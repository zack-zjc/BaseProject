package com.base.sdk.share.dialog

import android.app.Activity
import android.app.Dialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import com.base.sdk.share.R
import com.base.sdk.share.platform.SharePlatformInterface
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformSetting
import java.lang.ref.WeakReference

/**
 * author:zack
 * Date:2019/3/14
 * Description:null
 */
class ShareAdapter(private val activity: Activity,private val shareEntity: ShareEntity,private val platform: Array<PlatformSetting>,private var callback: Callback)
  : RecyclerView.Adapter<ShareHolder>() ,OnClickListener{

  private lateinit var dialogReference:WeakReference<Dialog>

  fun setDialogReference(dialog:Dialog){
    dialogReference = WeakReference(dialog)
  }

  override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ShareHolder {
    return ShareHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_share_item_view
        ,parent,false))
  }

  override fun getItemCount(): Int {
    return platform.size
  }

  override fun onBindViewHolder(holder: ShareHolder,position: Int) {
    holder.imageView.setImageResource(platform[position].getPlatformLogo())
    holder.textView.text = platform[position].getPlatformName()
    holder.itemView.tag = platform[position].getSharePlatform()
    holder.itemView.setOnClickListener(this)
  }

  override fun onClick(view: View?) {
    val platform = view?.tag
    if (platform is SharePlatformInterface){
      if (platform.isPlatformInstalled()){
        platform.registerApp(activity)
        platform.share(shareEntity,callback)
      }else{
        Toast.makeText(ApplicationContext.CONTEXT, R.string.str_share_app_miss_error, Toast.LENGTH_SHORT).show()
      }
      if (dialogReference.get() != null && dialogReference.get() is Dialog){
        (dialogReference.get() as Dialog).dismiss()
      }
    }
  }
}