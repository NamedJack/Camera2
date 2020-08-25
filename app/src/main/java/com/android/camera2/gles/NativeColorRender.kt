package com.android.camera2.gles

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NativeColorRender(private val color: Int) : GLSurfaceView.Renderer {


    override fun onDrawFrame(gl: GL10?) {
        onDrawFrame()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceChanged(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        surfaceCreate(color)
    }


    init {
        System.loadLibrary("native_color")
    }

    external fun onDrawFrame()

    external fun surfaceCreate(color: Int)

    external fun surfaceChanged(width: Int, height: Int)
}