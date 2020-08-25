package com.android.camera2.gles

import android.app.Activity
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import com.android.camera2.Camera2Application

object GlUtils {


    public fun checkGlVersion(): Boolean {
        val atyManager =
            Camera2Application.context?.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val configInfo = atyManager.deviceConfigurationInfo
        return configInfo.reqGlEsVersion >= 0x30000
    }

}