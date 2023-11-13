package com.resources.uploadlib.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.resources.uploadlib.R
import com.resources.uploadlib.databinding.ActivityCameraVideoBinding
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import java.io.File


@SuppressLint("InvalidWakeLockTag")
class CameraVideoActivity : AppCompatActivity() {

    val powerManager: PowerManager by lazy { getSystemService(Context.POWER_SERVICE) as PowerManager }
    val wakeLock by lazy {  powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "VideoCaptureWakeLock"); }


    val mBinding: ActivityCameraVideoBinding by lazy {
        DataBindingUtil.setContentView(
            this,
            R.layout.activity_camera_video
        )
    }
    val mCameraXUtil by lazy { CameraXUtil(this@CameraVideoActivity, mBinding.pvView) }
    var maxSecond = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        maxSecond = intent.getIntExtra("maxTime", 60)
        mBinding.apply {
            mActivity = this@CameraVideoActivity
            isVideoIng = false
            isEnd = false
            mCameraXUtil.startCamera()
            this.videoSeekBar.setOnSeekBarValueListener(object :
                VerticalSeekBar.OnSeekBarValueListener {
                override fun onProgress(value: Float) {
                    mCameraXUtil.mCameraControl?.apply {
                        this.setZoomRatio((mCameraXUtil.maxZoom - mCameraXUtil.minZoom) * value + mCameraXUtil.minZoom)
                    }
                }
            })
        }

        mBinding.mTime = "${mCameraXUtil.getFromTime(0)}/${mCameraXUtil.getFromTime(maxSecond)}"
        mBinding.cbVideoSwitch.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                startRecordingWithTimer()
            } else {
                stopRecording()
            }
        }

        mCameraXUtil.mutableFileBeanLiveDate.observe(this) {
            //刷新媒体库 否则选择照片时发现不了新的视频
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(it.filePath))).apply {
                sendBroadcast(this)
            }
            showVideo(it.filePath)
        }
    }


    val recordingTimer by lazy {
        object : CountDownTimer(maxSecond.toLong() * 1000, 1000) {
            override fun onTick(p0: Long) {
                mBinding.mTime = "${mCameraXUtil.getFromTime((maxSecond - p0 / 1000).toInt())}/${ mCameraXUtil.getFromTime(maxSecond)}"
            }
            override fun onFinish() {
                stopRecording()
            }
        }
    }

    private fun startRecordingWithTimer() {
        mBinding.isVideoIng = true
        mBinding.isEnd = false
        wakeLock.acquire(10*60*1000L)
        // 启动录制
        mCameraXUtil.takeVideo()
        // 启动计时器
        recordingTimer.start()
    }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        mBinding.isVideoIng = false
        mBinding.isEnd = true
        mCameraXUtil.cameraProvider.unbindAll()
        // 停止录制
        mCameraXUtil.videoCapture.stopRecording()
        // 停止计时器
        recordingTimer.cancel()
    }


    @SuppressLint("RestrictedApi")
    fun onItemClick(index: Int) {
        when (index) {
            0 -> {
                mCameraXUtil.switchCamera()
            }
        }
    }

    fun cameraOperate(index: Int) {
        when (index) {
            0 -> {//ok
                var mIntent = Intent()
                mCameraXUtil.mFileBean.apply {
                    mIntent.putExtra("fileName", this.fileName)
                    mIntent.putExtra("path", this.filePath)
                }
                setResult(-1, mIntent)
                finish()
            }
            1 -> {//cancel
                FileUtils.delete(mCameraXUtil.mFileBean.filePath)
                finish()
            }
        }
    }


    /**
     * 播放视频
     */
    fun showVideo(url: String) {
        var thumbImageView = ImageView(this)
        thumbImageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
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
        stopRecording()
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
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }


}