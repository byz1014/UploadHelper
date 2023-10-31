package com.resources.uploadlib.base
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout


/**
 * 屏幕宽度
 */
fun Context.width(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        var windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.currentWindowMetrics.bounds.width()
    } else {
        var metrics = resources.displayMetrics
        metrics.widthPixels
    }
}

/**
 * 屏幕高度
 */
fun Context.height(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        var windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.currentWindowMetrics.bounds.height()
    } else {
        var metrics = resources.displayMetrics
        metrics.heightPixels
    }
}

/**
 * dp转换px
 */
fun Context.dp2px(dpValue: Float): Float {
    var density = resources.displayMetrics.density
    return dpValue * density
}

/**
 * px转换dp
 */
fun Context.px2dp(pxValue: Float): Float {
    var density = resources.displayMetrics.density
    return pxValue / density
}


/**
 * 顶部软键盘高度
 */

fun Activity.topBarHeight():Int {
    var resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * 低部软键盘高度
 */

fun Activity.bottomBarHeight():Int {
    var resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}


/**
 * 页面高度
 */
fun Activity.contentBottom():Int{
   var context = window.decorView.findViewById<View>(android.R.id.content)
    return context.bottom
}
/**
 * 页面宽度
 */
fun Activity.contentRight():Int{
    var context = window.decorView.findViewById<View>(android.R.id.content)
    Log.e("checkCent_size","${context.left}       ${context.top}        ${context.right}        ${context.bottom}")
    return context.right
}





