package com.android.camera2.gles

import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.camera2.R
import kotlinx.android.synthetic.main.activity_glsurface.*

class GlSurfaceActivity : AppCompatActivity() {

    private lateinit var glSurface: GLSurfaceView

    companion object{
        const val GLSURFACE_VERSION = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_glsurface)


        glSurface =  GLSurfaceView(this)

        if (!GlUtils.checkGlVersion()) {
            Toast.makeText(this,"not support gl3.0 ",Toast.LENGTH_SHORT).show()
            return
        }

        glSurface.setEGLContextClientVersion(GLSURFACE_VERSION)
//        glSurface.setRenderer(NativeColorRender(Color.GREEN))
        glSurface.setRenderer(SimpleRender())
        setContentView(glSurface)
    }

    override fun onResume() {
        super.onResume()
        glSurface.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurface.onPause()
    }

}