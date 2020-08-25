package com.android.camera2

import android.media.Image
import android.opengl.Matrix

object NdkTools {
//    val stringFromNdk: String?
//        external get
//
//    external fun getImageFromNdk(image: Image?): String?

    init {
        System.loadLibrary("ndk_library")
    }
}