package com.android.camera2

import android.app.Application
import android.content.Context

class Camera2Application:Application() {

    companion object{
        var context:Context? =null
    }

    init {
        context = this
    }

}