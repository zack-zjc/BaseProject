package com.base.sdk.base.util

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import com.base.sdk.base.R
import com.zhl.cbdialog.CBDialogBuilder

/**
 * author:zack
 * Date:2019/5/16
 * Description:展示权限说明弹框
 */
object PermissionDialogUtil {

  /**
   * 跳转展示说明弹框
   * @param context:context对象
   * @param permissionTitle:弹框标题
   * @param permissionDesc 文字说明
   * @param refusePermissionAction 拒绝操作
   * @param confirmPermissionAction 同意操作
   */
  fun jumpPermissionExplainDialog(context: Context,permissionTitle:CharSequence,permissionDesc:CharSequence,
    refusePermissionAction:()->Unit,confirmPermissionAction:()->Unit){
    val view = LayoutInflater.from(context).inflate(R.layout.layout_main_base_permisssion_explain_dialog,null)
    val messageView = view.findViewById<TextView>(R.id.id_main_base_permission_message)
    messageView.text = permissionDesc
    val dialog = CBDialogBuilder(context, CBDialogBuilder.DIALOG_STYLE_NORMAL)
        .setTouchOutSideCancelable(false)
        .setCancelable(false)
        .showIcon(false)
        .setTitle(permissionTitle)
        .setView(view)
        .showCancelButton(true)
        .setCancelButtonTextColor(Color.parseColor("#999999"))
        .setButtonClickListener(true) { context, dialog, position ->
          if (position == 1){
            refusePermissionAction()
          }else{
            confirmPermissionAction()
          }
        }
        .create()
    dialog.show()
  }

}