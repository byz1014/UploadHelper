package com.resources.uploadlib.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import com.resources.uploadlib.ChooseDialog
import com.resources.uploadlib.adapter.UploadAdapter
import com.resources.uploadlib.bean.RequestCodeBean
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.choose.ChooseActionState
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.http.UpLoadTask
import com.resources.uploadlib.util.LocalMedia2ResourcesBean
import java.io.File


/**
 * @Author:BYZ
 * @Time:2023/10/19 9:45
 * @blame Android Team
 * @info
 */


fun ResourcesBean.onUpLoad(outAction: (mBean: ResourcesBean) -> Unit) {
    UpLoadTask(outAction).initUploadTask(this)
}

var addResourcesBean = ResourcesBean().apply {
    this.type = ResourcesType.ADD
    this.isUpload = true
    this.state = ResourcesState.UPLOAD_SUCCESS
}

fun Activity.showChooseDialog(mList:MutableList<ChooseActionState>) {
    ChooseDialog(this, mList)
}

@RequiresApi(Build.VERSION_CODES.N)
fun UploadAdapter.checkAddItem(start: Int = -1, fileMax:Int = 10000) {
    data.removeIf { it.type == ResourcesType.ADD }
    if (getIsLooker()) {
        notifyDataSetChanged()
        return
    }
    if (data.size > 0) {
        if (data[data.size - 1].type != ResourcesType.ADD && data.size < fileMax) {
            data.add(addResourcesBean)
        }
    } else if (data.size == 0) {
        data.add(addResourcesBean)
    }

    if (start == -1) {
        notifyDataSetChanged()
    } else {//删除任务
        removeAt(start)
        this.notifyItemRemoved(start)
    }
}

/**
 * 处理拍照返回
 */
fun Intent.getResourcesBean():ResourcesBean{
    var filePath = this.getStringExtra("path")
    var mFileName = this.getStringExtra("fileName")
    return ResourcesBean().apply {
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
    }
}



@RequiresApi(Build.VERSION_CODES.N)
fun UploadAdapter.addResources(resList: List<LocalMedia>) {
    Log.e("metaRTC", "--------------   ${resList.size}")
    data.removeIf { it.type == ResourcesType.ADD }
    resList.forEach { bean ->
        //去重添加
//        if (bean.mimeType.contains("video")) {
//            data.any { it.localPath == bean.realPath }
//        } else {
//            data.any { it.localPath == bean.compressPath }
//        }.apply {
//            if (!this) {
//                data.add(bean.LocalMedia2ResourcesBean())
//            }
//        }
        //允许重复
        data.add(bean.LocalMedia2ResourcesBean())

    }
    checkAddItem()
}

var mStateList = mutableListOf<ChooseActionState>(
    ChooseActionState.CHOOSE_CAMERA,
    ChooseActionState.CHOOSE_PICTURE,
    ChooseActionState.CHOOSE_VIDEO,
    ChooseActionState.CHOOSE_TAKE_PHOTO
)


fun PermissionUtils.onPermission(action: () -> Unit) {
    callback(object :
        PermissionUtils.SimpleCallback {
        override fun onGranted() {
            action()
        }
        override fun onDenied() {
            ToastUtils.showShort("缺少必要权限，请手动授权")
        }
    }).request()
}
/**
 * 1.编辑 只读
 * 2.最大数量
 * 3.视频最大长度
 * 4.视频最大内存
 * 5.图片大小
 * 6.Dialog 内容（list）
 * ---------------------
 * 7.网络请求相关
 */



