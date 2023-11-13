package com.resources.uploadlib.camera

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import android.util.Size
import android.view.Surface.ROTATION_0
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.resources.uploadlib.base.height
import com.resources.uploadlib.base.width
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max


/**
 * @Author:BYZ
 * @Time:2023/10/13 13:40
 * @blame Android Team
 * @info
 */
@SuppressLint("RestrictedApi")
class CameraXUtil(var mContext: AppCompatActivity, var surfaceProv: PreviewView) {
    val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(mContext) }
    val cameraProvider: ProcessCameraProvider by lazy { cameraProviderFuture.get() }//相机信息
    var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA//当前相机
    val imageCapture: ImageCapture by lazy { ImageCapture.Builder().build() }//拍照用例
    val videoCapture: VideoCapture by lazy {
        VideoCapture.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetResolution(Size(mContext.width(), mContext.height()))
            .setAudioRecordSource(MediaRecorder.AudioSource.MIC)
            .setBitRate(3000 * 1024 * 1) // 3*8*1000*1024
            .setVideoFrameRate(30)
            .setTargetRotation(ROTATION_0)
            .build()
    }//录像用例 24000     29.76   4 13.7  5

    var mCameraControl: CameraControl? = null

    val preview by lazy { Preview.Builder().build() }

    var maxZoom = 0f
    var minZoom = 0f
    var mCamera:Camera?=null
    /**
     * 初始化像机
     */
    fun startCamera() {
        cameraProviderFuture.addListener({
            preview.setSurfaceProvider(surfaceProv.surfaceProvider)
            cameraProvider.unbindAll()
              mCamera = cameraProvider.bindToLifecycle(
                mContext,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            ).apply {
                this.cameraInfo.zoomState.value?.apply {
                    minZoom = this.minZoomRatio
                    maxZoom = this.maxZoomRatio
                }
                mCameraControl = this.cameraControl
            }
        }, ContextCompat.getMainExecutor(mContext))
    }


    var mFileBean = FileBean()
    var mutableFileBeanLiveDate = MutableLiveData<FileBean>()
    /**
     * 录制视频
     */
    fun takeVideo() {
        //视频保存路径
        Log.e("metaRTC", "开始录制")
        var file = mContext.createFile("yyyy-MM-dd-HH-mm-ss", "mp4")
        var options: VideoCapture.OutputFileOptions =
            VideoCapture.OutputFileOptions.Builder(file).build()
        //开始录像
        videoCapture.startRecording(options, Executors.newSingleThreadExecutor(), object :
            VideoCapture.OnVideoSavedCallback {

            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                mFileBean.apply {
                    fileName = file.name
                    filePath = file.path
                    mutableFileBeanLiveDate.postValue(this)

                }
            }
            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                //保存失败的回调，可能在开始或结束录制时被调用
                Log.e(
                    "metaRTC",
                    "error   $message      $videoCaptureError    ${cause?.message}"
                )
            }
        })
    }

    /***
     * 切换摄像头
     */
    fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA; // 切换到前置摄像头
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA; // 切换到后置摄像头
        }
        cameraProvider.unbindAll()
        mCamera = cameraProvider.bindToLifecycle(
            mContext,
            cameraSelector,
            preview,
            imageCapture,
            videoCapture
        ).apply {

            mCameraControl = this.cameraControl
        }//绑定用例
    }

    /**
     * 时间格式化
     * s --> mm:ss
     */
    fun getFromTime(mTime: Int): String {
        var maxM = if (mTime / 60 < 10) {
            "0${mTime / 60}"
        } else {
            "${mTime / 60}"
        }
        var maxS = if (mTime % 60 < 10) {
            "0${mTime % 60}"
        } else {
            "${mTime % 60}"
        }

        return "$maxM:$maxS"
    }
}

class FileBean {
    var fileName = ""
    var filePath = ""
}