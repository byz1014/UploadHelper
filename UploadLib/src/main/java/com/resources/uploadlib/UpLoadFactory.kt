package com.resources.uploadlib

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.resources.uploadlib.adapter.UploadAdapter
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.choose.ChooseActionState
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.http.UpLoadTask
import com.resources.uploadlib.util.ResConfig
import java.io.File


/**
 * @Author:BYZ
 * @Time:2023/10/26 17:09
 * @blame Android Team
 * @info
 */
class UpLoadFactory(var mActivity:Activity) {
    private val uploadAdapter by lazy { UploadAdapter(arrayListOf()) }

    //初始化上传任务
    private val mUpLoadTask by lazy {
        UpLoadTask {
            var index = uploadAdapter.getItemPosition(it)
            notifyAdapter(index)
             if(uploadAdapter.getItem(index).state == ResourcesState.UPLOAD_START ||uploadAdapter.getItem(index).state == ResourcesState.UPLOAD_ING){
               return@UpLoadTask
            }
            toUploadFile(index + 1)
        }
    }

    //接收并处理当前任务
    private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (uploadAdapter.data.size > msg.what) {
                if (uploadAdapter.data[msg.what].isUpload) {
                    toUploadFile(msg.what + 1)
                } else {
                    mUpLoadTask.initUploadTask(uploadAdapter.data[msg.what])
                }
            }
        }
    }

   // 通知进行一次上传任务
    private fun toUploadFile(index: Int) {
        mHandler.sendEmptyMessage(index)
    }

    //在UI线程刷新适配器
    private fun notifyAdapter(index:Int){
        mActivity.runOnUiThread{
            uploadAdapter.notifyItemChanged(index)
        }
    }

    /**
     * 绑定RecycleView
     * max 可上传内容的最大值
     */
    fun addAdapter(mRecycle: RecyclerView, activity: Activity ) {
        mRecycle.layoutManager = GridLayoutManager(activity, 3)
        mRecycle.adapter = uploadAdapter
        uploadAdapter.checkAddItem()
    }

    /**
     * 处理 onActivityResult 返回的数据
     */
    fun onResultData(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ResConfig.CAMERA_CODE) {
            data?.apply {
                var filePath = this.getStringExtra("path")
                var mFileName = this.getStringExtra("fileName")
                ResourcesBean().apply {
                    mediaType = "video/mp4"
                    filePath?.let {
                        this.localPath = it
                        this.fileSize = "${File(it).length() / 1024 / 1024}M"
                    }
                    this.state = ResourcesState.UPLOAD_START
                    this.type = ResourcesType.VIDEO
                    this.isUpload = false
                    mFileName?.apply {
                        fileName = this
                    }
                    uploadAdapter.data.removeIf { it.type == ResourcesType.ADD }
                    uploadAdapter.data.add(this)
                    var mIndex = uploadAdapter.data.size
                    uploadAdapter.checkAddItem()
                    toUploadFile(mIndex - 1)
                }
            }

        } else {
            val selectList = PictureSelector.obtainMultipleResult(data)
            Log.e("metaRTC", "${Gson().toJson(selectList)}")
            uploadAdapter.addResources(selectList)
            toUploadFile(0)
        }
    }


    /**
     * 赋值
     * 一般用于详情时读取远程文件
     */
   fun setData(mList:MutableList<ResourcesBean>){
       uploadAdapter.data.addAll(mList)
       uploadAdapter.checkAddItem()
    }


    /**
     * 获取当前文件
     */
    fun getMediaData(): MutableList<ResourcesBean> {
        var mList = mutableListOf<ResourcesBean>()
        uploadAdapter.data.forEach {
            if(it.type != ResourcesType.ADD){
                mList.add(it)
            }
        }
        return mList
    }


    /**
     * 设置弹出框的内容
     * 默认 全功能
     */
    fun setDialogList(list:MutableList<ChooseActionState>){
       DIALOG_ACTION_LIST.clear()
        DIALOG_ACTION_LIST.addAll(list)
    }

    /**
     * 设置视频（拍摄和选择）的时长
     * 默认15S
     */
    fun setVideoMaxSecond(mMaxSecond:Int){
        VIDEO_MAX_SECOND = mMaxSecond
    }

    /**
     * 设置是否为观察者
     * true 不可编辑
     * false 可编辑
     */
    fun setLooker(looker:Boolean){
        IS_LOOKER = looker
    }

    /**
     * 设置最大数量
     * 默认 9
     */
    fun setMax(max:Int){
        FILE_MAX = max
    }

    /**
     * 设置视频大小内存 M
     * 默认 100M
     */
    fun setVideoMaxSize(size:Float){
        VIDEO_MAX_SIZE = size
    }

    /**
     * 设置图片最大内存 M
     * 默认5M
     */
    fun setPictureMaxSize(size:Float){
        PICTURE_MAX_SIZE = size
    }




}