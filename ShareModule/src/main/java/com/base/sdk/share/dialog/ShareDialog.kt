package com.base.sdk.share.dialog

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.base.sdk.share.R
import com.base.sdk.share.callback.Callback
import com.base.sdk.share.callback.DefaultCallback
import com.base.sdk.share.config.ApplicationContext
import com.base.sdk.share.entity.ShareEntity
import com.base.sdk.share.platform.PlatformSetting
import com.zhl.cbdialog.CBDialogBuilder

/**
 * author:zack
 * Date:2019/3/13
 * Description:分享的dialog
 */
object ShareDialog {

  /**
   * 展示分享的dialog
   * 可自定义dialog修改这里
   */
  fun showShareDialog(context: Activity,shareEntity: ShareEntity,platform: Array<PlatformSetting>,callback: Callback=DefaultCallback()){
    val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_share_dialog_view,null)
    val recyclerView = dialogView.findViewById<RecyclerView>(R.id.id_share_recycler_view)
    recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
    val adapter = ShareAdapter(context,shareEntity,platform,callback)
    recyclerView.adapter = adapter
    val dialog = CBDialogBuilder(context, CBDialogBuilder.DIALOG_STYLE_NORMAL, 1.0f)
        .setTouchOutSideCancelable(true)
        .showIcon(false)
        .setTitle(ApplicationContext.CONTEXT.resources.getString(R.string.str_share_message_to))
        .setConfirmButtonText(ApplicationContext.CONTEXT.resources.getString(R.string.str_share_cancel))
        .setDialoglocation(CBDialogBuilder.DIALOG_LOCATION_BOTTOM)
        .setView(dialogView)
        .setDialogAnimation(CBDialogBuilder.DIALOG_ANIM_SLID_BOTTOM)
        .create()
    adapter.setDialogReference(dialog)
    dialog.show()
  }

}