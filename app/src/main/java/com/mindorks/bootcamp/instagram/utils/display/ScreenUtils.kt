package com.mindorks.bootcamp.instagram.utils.display

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.mindorks.bootcamp.instagram.R


object ScreenUtils : ScreenResourceProvider{

    override fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

    override fun getScreenHeight() = Resources.getSystem().displayMetrics.heightPixels

    fun setLightDisplayStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags =
                activity.window.decorView.systemUiVisibility // get current flag
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // add LIGHT_STATUS_BAR to flag
            activity.window.decorView.systemUiVisibility = flags
            activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.colorStatus)
        }
    }
}