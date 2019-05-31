package com.base.sdk.base.util

import android.os.Build
import android.view.Window
import android.view.WindowManager

/**
 * Created by zack on 2019/2/20.
 * 设备相关的操作方法
 */

object DeviceUtil {

    /**
     * 是否是小米的手机
     */
    fun isXiaoMi() = Build.MANUFACTURER.toUpperCase().contains("XIAOMI")

    /**
     * 是否是华为手机
     */
    fun isHuaWei() = Build.MANUFACTURER.toUpperCase().contains("HUAWEI")

    /**
     * 是否是oppo手机
     */
    fun isOPPO() = Build.MANUFACTURER.toUpperCase().contains("OPPO")

    /**
     * 是否是vivo手机
     */
    fun isVIVO() = Build.MANUFACTURER.toUpperCase().contains("VIVO")

    /**
     * 是否是魅族手机
     */
    fun isFlyme() = Build.MANUFACTURER.toUpperCase().contains("MEIZU")

    /**
     * 是否是三星手机
     */
    fun isSAMSUNG() = Build.MANUFACTURER.toUpperCase().contains("SAMSUNG")

    /**
     * 是否是LG手机
     */
    fun isLG() = Build.MANUFACTURER.toUpperCase().contains("LG")

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun setFlymeStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        window?.let {
            try {
                val lp = it.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                it.attributes = lp
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6,android4.4以上
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun setMIUIStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        window?.let {
            val clazz = it.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                val darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (dark) {
                    extraFlagField.invoke(it, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(it, 0, darkModeFlag)//清除黑色字体
                }
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * 获取statusBar高度
     */
    fun getStatusBarHeight() :Int {
        val resourceId = ApplicationContext.CONTEXT.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return ApplicationContext.CONTEXT.resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    /**
     * 是否是刘海屏
     * true 是 false otherwise
     */
    fun isNotchScreen() :Boolean {
        try {
            if (isHuaWei()){
                val HwNotchSizeUtil = ApplicationContext.CONTEXT.classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
                return get.invoke(HwNotchSizeUtil) as Boolean
            } else if (isOPPO()){
                return ApplicationContext.CONTEXT.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
            } else if (isVIVO()){
                val FtFeature = ApplicationContext.CONTEXT.classLoader.loadClass("android.util.FtFeature")
                val get = FtFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
                return get.invoke(FtFeature, 0x00000020) as Boolean
            } else if (isXiaoMi()){
                val pro = ApplicationContext.CONTEXT.classLoader.loadClass("android.os.SystemProperties")
                val get = pro.getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
                return (get.invoke(pro, "ro.miui.notch", 0) as Int) == 1
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false
    }

}