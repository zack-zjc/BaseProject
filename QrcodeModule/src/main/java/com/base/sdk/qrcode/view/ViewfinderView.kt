package com.base.sdk.qrcode.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.base.sdk.qrcode.R
import com.base.sdk.qrcode.camera.CameraManager

/**
 * author:zack
 * Date:2019/4/22
 * Description:ViewfinderView
 */
class ViewfinderView @JvmOverloads constructor(context: Context,attributes: AttributeSet? =null,defStyleAttr: Int = 0)
  : View(context,attributes,defStyleAttr) {

  private val ANIMATION_DELAY = 10L
  private val CORNER_WIDTH = 5
  private val SPEEN_DISTANCE = 5
  private var ScreenRate: Int = 0

  private var paint: Paint = Paint()

  private var slideTop: Int = 0

  private var lineRect: RectF = RectF()

  private var maskColor: Int = Color.parseColor("#60000000")

  private var dividerColor :Int = Color.parseColor("#7cbd1e")

  private var angleColor :Int = Color.parseColor("#ffde08")

  private var isFirst: Boolean = true

  init {
    val density = context.resources.displayMetrics.density
    ScreenRate = (15 * density).toInt()
    val attr = context.obtainStyledAttributes(attributes, R.styleable.ViewfinderView)
    maskColor = attr.getColor(R.styleable.ViewfinderView_maskColor,maskColor)
    dividerColor = attr.getColor(R.styleable.ViewfinderView_dividerColor,dividerColor)
    angleColor = attr.getColor(R.styleable.ViewfinderView_angleColor,angleColor)
    attr.recycle()
  }

  override fun onDraw(canvas: Canvas) {
    val frame = CameraManager.get()?.getFramingRect()
    if (frame != null){
      if (isFirst) {
        isFirst = false
        slideTop = frame.top
      }
      val width = width.toFloat()
      val height = height.toFloat()
      paint.color = maskColor
      canvas.drawRect(0F, 0F, width, frame.top.toFloat(), paint)
      canvas.drawRect(0F, frame.top.toFloat(), frame.left.toFloat(), (frame.bottom + 1).toFloat(), paint)
      canvas.drawRect((frame.right + 1).toFloat(), frame.top.toFloat(), width, (frame.bottom + 1).toFloat(), paint)
      canvas.drawRect(0F, (frame.bottom + 1).toFloat(), width, height, paint)
      paint.color = angleColor
      canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + ScreenRate).toFloat(),(frame.top + CORNER_WIDTH).toFloat(), paint)
      canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + CORNER_WIDTH).toFloat(), (frame.top + ScreenRate).toFloat(), paint)
      canvas.drawRect((frame.right - ScreenRate).toFloat(), frame.top.toFloat(), frame.right.toFloat(),(frame.top + CORNER_WIDTH).toFloat(), paint)
      canvas.drawRect((frame.right - CORNER_WIDTH).toFloat(), frame.top.toFloat(), frame.right.toFloat(), (frame.top + ScreenRate).toFloat(), paint)
      canvas.drawRect(frame.left.toFloat(), (frame.bottom - CORNER_WIDTH).toFloat(), (frame.left + ScreenRate).toFloat(), frame.bottom.toFloat(), paint)
      canvas.drawRect(frame.left.toFloat(), (frame.bottom - ScreenRate).toFloat(),(frame.left + CORNER_WIDTH).toFloat(), frame.bottom.toFloat(), paint)
      canvas.drawRect((frame.right - ScreenRate).toFloat(), (frame.bottom - CORNER_WIDTH).toFloat(),frame.right.toFloat(), frame.bottom.toFloat(), paint)
      canvas.drawRect((frame.right - CORNER_WIDTH).toFloat(), (frame.bottom - ScreenRate).toFloat(),frame.right.toFloat(), frame.bottom.toFloat(), paint)
      slideTop += SPEEN_DISTANCE
      if (slideTop >= frame.bottom-2) {
        slideTop = frame.top
      }
      lineRect.set(frame.left.toFloat(), slideTop.toFloat(), frame.right.toFloat(), (slideTop + 2).toFloat())
      paint.color = dividerColor
      canvas.drawRoundRect(lineRect,1F,1F,paint)
      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom)
    }
  }


}