package com.android.camera2

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.util.Size

object Camera2Helper {


    private var camera2Manager: CameraManager? = null

    init {
        camera2Manager =
            Camera2Application.context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }


    fun getFrontCameraId(): String {
        return getCameraId(true)
    }

    fun getBackCameraId():String{
        return getCameraId(false)
    }


    fun getCameraId(useFront: Boolean): String {
        try {
            val cameraIdList = camera2Manager!!.cameraIdList
            for (cid in cameraIdList) {
                val cameraCharacteristics = camera2Manager?.getCameraCharacteristics(cid)
                val facing = cameraCharacteristics?.get(CameraCharacteristics.LENS_FACING)
                if (useFront) {
                    if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        return cid
                    }
                } else {
                    if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        return cid
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }


    fun getOutputSize(cameraId: String, clz: Class<Any>): Array<out Size>? {
        try {
            val cameraCharacteristics = camera2Manager?.getCameraCharacteristics(cameraId)
            val characteristics =
                cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val outputSizes = characteristics?.getOutputSizes(clz)
            return outputSizes
        } catch (e: Exception) {
            return null
        }

    }

    fun getOutputSize(cameraId: String, format: Int): Array<Size>? {
        try {
            val cameraCharacteristics = camera2Manager?.getCameraCharacteristics(cameraId)
            val characteristics =
                cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val outputSizes = characteristics?.getOutputSizes(format)
            return outputSizes
        } catch (e: Exception) {
            return null
        }
    }

    fun releaseCamera2(cameraDevice: CameraDevice) {
        if (cameraDevice != null) {
            cameraDevice.close()
            cameraDevice == null
        }
    }

}