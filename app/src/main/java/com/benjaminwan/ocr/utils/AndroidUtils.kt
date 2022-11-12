package com.benjaminwan.ocr.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager

/**
 * dp转px
 * @param  value dp值
 * @return 返回px值
 */
fun dp2px(context: Context, value: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (value.toFloat() * scale + 0.5f).toInt()
}

/**
 * dp转px
 * @param  value dp值
 * @return 返回px值
 */
fun Activity.dp2px(value: Int): Int {
    return dp2px(this.applicationContext, value)
}

/**
 * 获取app的VersionCode
 * @return VersionCode
 */
fun getAppVersionCode(context: Context): Long {
    var appVersionCode: Long = 0
    try {
        val packageInfo = context.applicationContext
            .packageManager
            .getPackageInfo(context.packageName, 0)
        appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return appVersionCode
}

/**
 * 获取app的versionName
 * @return versionName
 */
fun getAppVersionName(context: Context): String {
    var appVersionName = ""
    try {
        val packageInfo = context.applicationContext
            .packageManager
            .getPackageInfo(context.packageName, 0)
        appVersionName = packageInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return appVersionName
}

/**
 * 检测是否安装[packageName]的app
 * @param packageName 报名
 * @return Boolean
 */
fun isAppInstalled(context: Context, packageName: String): Boolean {
    val pm = context.packageManager
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

/**
 * 设置保持屏幕常亮
 */
fun Activity.setKeepScreenOn() {
    this.window.setFlags(
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    )
}

/**
 * 判断是否平板设备
 */
fun isTabletDevice(context: Context): Boolean {
    return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >=
            Configuration.SCREENLAYOUT_SIZE_LARGE
}

/**
 * 获取屏幕宽度
 */
/*fun getScreenWidth(context: Activity): Int =
    if (Build.VERSION.SDK_INT >= 30) {
        val displayMetrics = DisplayMetrics()
        val display = context.display
        display!!.getRealMetrics(displayMetrics)
        displayMetrics.widthPixels
    } else {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }*/

/**
 * 获取屏幕高度
 */
/*fun getScreenHeight(context: Activity): Int =
    if (Build.VERSION.SDK_INT >= 30) {
        val displayMetrics = DisplayMetrics()
        val display = context.display
        display!!.getRealMetrics(displayMetrics)
        displayMetrics.heightPixels
    } else {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }*/
