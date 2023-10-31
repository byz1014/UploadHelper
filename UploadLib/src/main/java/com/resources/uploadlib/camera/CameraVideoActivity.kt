package com.resources.uploadlib.camera

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.resources.uploadlib.R
import com.resources.uploadlib.VIDEO_MAX_SECOND
import com.resources.uploadlib.databinding.ActivityCameraVideoBinding
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import java.io.File

/**
 * create by byz
 * 拍摄视频
 */
class CameraVideoActivity : AppCompatActivity() {
    val mBinding: ActivityCameraVideoBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_camera_video) }
    val mCameraXUtil by lazy {  CameraXUtil(this@CameraVideoActivity,mBinding.pvView) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.apply {
            mActivity = this@CameraVideoActivity
            isVideoIng = false
            isEnd = false
            mCameraXUtil.startCamera()
            this.videoSeekBar.setOnSeekBarValueListener(object :
                VerticalSeekBar.OnSeekBarValueListener {
                override fun onProgress(value: Float) {
                    mCameraXUtil.mCameraControl?.apply {
                        this.setZoomRatio((mCameraXUtil.maxZoom - mCameraXUtil.minZoom)*value + mCameraXUtil.minZoom)
                    }
                }
            })
        }

        mBinding.mTime = "${mCameraXUtil.getFromTime(0)}/${mCameraXUtil.getFromTime(VIDEO_MAX_SECOND)}"
        mBinding.cbVideoSwitch.setOnCheckedChangeListener { p0, p1 ->
            if(p1){
                valueTime = 0
                mHandler.sendEmptyMessage(0)
                mBinding.isVideoIng = true
                mCameraXUtil.takeVideo()
            }else{
                mHandler.sendEmptyMessage(-1)
            }
        }

        mCameraXUtil.mutableFileBeanLiveDate.observe(this){
            //刷新媒体库 否则选择照片时发现不了新的视频
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(it.filePath))).apply {
                sendBroadcast(this)
            }
            showVideo(it.filePath)
        }
    }


    @SuppressLint("RestrictedApi")
    fun onItemClick(index:Int){
        when(index){
            0->{
                mCameraXUtil.switchCamera()
            }
        }
    }

    var valueTime = 0
    @SuppressLint("RestrictedApi")
    var mHandler = Handler(Looper.getMainLooper()){
        if(it.what == -1){
            stopReport()
            return@Handler  true
        }
        valueTime++
        if(valueTime> VIDEO_MAX_SECOND){
            stopReport()
            return@Handler  true
        }
        sendHandler()
        mBinding.mTime = "${mCameraXUtil.getFromTime(valueTime)}/${mCameraXUtil.getFromTime(VIDEO_MAX_SECOND)}"
        return@Handler  true
    }

    fun sendHandler(){
        mHandler.sendEmptyMessageDelayed(0,996)
    }

    @SuppressLint("RestrictedApi")
    fun stopReport(){
        mBinding.isVideoIng = false
        mBinding.isEnd = true
        mCameraXUtil.cameraProvider.unbindAll()
        mHandler.removeMessages(0)
        mCameraXUtil.videoCapture.stopRecording()
    }


    fun cameraOperate(index: Int){
        Log.e("metaRTC","      $index      ")
        when(index){
            0->{//ok
                var mIntent = Intent()
                mCameraXUtil.mFileBean.apply {
                    mIntent.putExtra("fileName",this.fileName)
                    mIntent.putExtra("path",this.filePath)
                }
                setResult(-1,mIntent)
                finish()
            }
            1->{//cancel
                finish()
            }
        }
    }


    /**
     * 播放视频
     */
    fun showVideo(url:String){
        var thumbImageView = ImageView(this)
        thumbImageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
        url.getFirstFrame()?.apply {
            thumbImageView.setImageBitmap(this)
        }

        GSYVideoOptionBuilder().apply {
            setThumbImageView(thumbImageView)
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(url)
                .setCacheWithPlay(false)
                .build(mBinding.svpDetailPlayer)
        }
        //增加title
        mBinding.svpDetailPlayer.titleTextView.visibility = View.GONE
        mBinding.svpDetailPlayer.backButton.visibility = View.GONE
        mBinding.svpDetailPlayer.fullscreenButton.visibility = View.GONE
    }

    override fun onPause() {
        mBinding.svpDetailPlayer.currentPlayer.onVideoPause()
        super.onPause()


    }

    override fun onResume() {
        mBinding.svpDetailPlayer.currentPlayer.onVideoResume(false)
        super.onResume()

    }

    override fun onDestroy() {
        mBinding.svpDetailPlayer.currentPlayer.release()
        super.onDestroy()
    }




}