package com.android.camera2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class CameraActivity : AppCompatActivity() {

    //1.cameraManager
    //2.cameraCharacteristics
    //3.cameraCaptureSessions
    //4.surface 设置预览的尺寸和格式  相机所能支持的尺寸

    private lateinit var cameraManager: CameraManager
    private lateinit var currentCameraCharacteristics: CameraCharacteristics
    private var cameraCaptureSession: CameraCaptureSession? = null

    private var currentCameraId = ""
    private var cameraDevice: CameraDevice? = null
    private var imageReader: ImageReader? = null
    private lateinit var file: File

    private var backThread: HandlerThread? = null
    private var backHandler: Handler? = null

    private var previewSizeList: Array<Size>? = null

    //使用后置摄像头
    private val cameraFaceBack = CameraCharacteristics.LENS_FACING_BACK


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        startBackgroundThread()
        file = File(getExternalFilesDir(null), "pic.jpg")
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        textureView.surfaceTextureListener = surfaceTextureListener

        changePreviewSize.setOnClickListener {
            changePreviewSize()
            createCameraPreviewSession()
        }
    }

    var index = 0
    private fun changePreviewSize() {
        val size = previewSizeList!!.size
        if (index + 1 < size) {
            index++
        } else {
            index = 0
        }
        textureView.surfaceTexture.setDefaultBufferSize(
            previewSizeList!!.get(index).width,
            previewSizeList!!.get(index).height

        )
        previewSize.text =
            "${previewSizeList!!.get(index).width} + ${previewSizeList!!.get(index).height} "


    }

    private fun startBackgroundThread() {
        backThread = HandlerThread("cameraBackThread").also { it.start() }
        backHandler = Handler(backThread?.looper)
    }


    private fun stopBackgroundThread() {
        backThread?.quitSafely()
        try {
            backThread?.join()
            backThread = null
            backHandler = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        //预览的宽高度
        val previewSize = Size(1080, 1920)

    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?, width: Int, height: Int
        ) {
//            configTransform(width, height)
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            //releaseCamera()
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            //预览surfaceTextureView 可用时 打开相机

            initCameraInfo(width, height)
//            configureTransform(width, height)
            openCamera()
        }
    }

    private fun configTransform(width: Int, height: Int) {

    }

    private fun configureTransform(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    private val cameraStateCallBack = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            onDisconnected(camera)
        }
    }


    private fun createCameraPreviewSession() {
        val captureRequestBuilder =
            cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        val surface = Surface(textureView.surfaceTexture)
        captureRequestBuilder?.addTarget(surface)
        captureRequestBuilder?.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_AUTO
        )
        cameraDevice?.createCaptureSession(
            arrayListOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.d("camera", "开启预览会话失败！")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    cameraCaptureSession?.setRepeatingRequest(
                        captureRequestBuilder!!.build(),
                        captureCallback,
                        backHandler
                    )
                }

            },
            backHandler
        )
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            val canExchangeCamera = true
            val canTakePic = true
        }


        override fun onCaptureFailed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            failure: CaptureFailure
        ) {
            super.onCaptureFailed(session, request, failure)
            Log.d("camera", "开启预览失败！")
        }
    }


    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        backHandler?.post(ImageServer(it.acquireNextImage(), file))
    }


    private fun initCameraInfo(width: Int, height: Int) {
        // 使用cameraManager 获取所有的摄像头和分别的对应 cameraCharacteristics 参数信息
        val cameraIdList = cameraManager.cameraIdList
        for (id in cameraIdList) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
            //使用后置摄像头
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFaceBack) {
                currentCameraCharacteristics = cameraCharacteristics
                currentCameraId = id
                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                val configurationMap =
                    cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                //判断相机是否支持camera2的高级特性
//                val supportedLevel =
//                    cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
//                if (supportedLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL ||
//                    supportedLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
//                ) {
//                    //建议使用camera1
//                }

//                val asList = asList(*configurationMap?.getOutputSizes(SurfaceTexture::class.java))
//                for (i in asList) {
//                    Log.d("supportSize",i.toString())
//                }
                val previewSizes = configurationMap?.getOutputSizes(SurfaceTexture::class.java)
                previewSizeList = configurationMap?.getOutputSizes(SurfaceTexture::class.java)


                val preLargest = Collections.max(
                    asList(*previewSizes),
                    CompareSizesByArea()
                )
                val saveLargest = Collections.max(
                    asList(*configurationMap?.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByArea()
                )


                textureView.surfaceTexture.setDefaultBufferSize(
                    previewSizes!!.get(0).width,
                    previewSizes!!.get(0).height
                )

                imageReader =
                    ImageReader.newInstance(
                        preLargest.width,
                        preLargest.height,
                        ImageFormat.JPEG,
                        2
                    )
                        .apply {
                            setOnImageAvailableListener(
                                onImageAvailableListener,
                                backHandler
                            )
                        }
            }
        }


    }

    private fun getOptionSize(outputSizes: Array<Size>?, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        cameraManager.openCamera(currentCameraId, cameraStateCallBack, backHandler)
    }


    override fun onStop() {
        super.onStop()
        releaseCamera2()
        stopBackgroundThread()
    }

    private fun releaseCamera2() {

        try {
//            cameraOpenCloseLock.acquire()
            cameraCaptureSession?.close()
            cameraCaptureSession = null
            cameraDevice?.close()
            cameraDevice = null
//            imageReader?.close()
//            imageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
//            cameraOpenCloseLock.release()
        }
    }

}