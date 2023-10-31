package com.resources.uploadlib

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import com.resources.uploadlib.adapter.UploadAdapter
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.choose.ChooseActionState
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType
import com.resources.uploadlib.http.UpLoadTask
import com.resources.uploadlib.util.LocalMedia2ResourcesBean


/**
 * @Author:BYZ
 * @Time:2023/10/19 9:45
 * @blame Android Team
 * @info
 */

/**
 * 开启一个上传任务
 */
fun ResourcesBean.onUpLoad(outAction: (mBean: ResourcesBean) -> Unit) {
    UpLoadTask(outAction).initUploadTask(this)
}

/**
 * 这是一个添加入口
 */
var addResourcesBean = ResourcesBean().apply {
    this.type = ResourcesType.ADD
    this.isUpload = true
    this.state = ResourcesState.UPLOAD_SUCCESS
}


/**
 * 调用弹框选择功能
 */
fun Activity.showChooseDialog(sum: Int) {
     ChooseDialog(this, DIALOG_ACTION_LIST.apply {
         if(isEmpty()){
             DIALOG_ACTION_LIST.addAll(mStateList)
         }
     }).apply {
        show(sum)
    }
}

/**
 * 每次添加或删除时检查是否要显示添加入口
 */
fun UploadAdapter.checkAddItem(start: Int = -1) {
    data.removeIf { it.type == ResourcesType.ADD }
    if (IS_LOOKER) {
        notifyDataSetChanged()
        return
    }
    if (data.size > 0) {
        if (data[data.size - 1].type != ResourcesType.ADD && data.size < FILE_MAX) {
            data.add(addResourcesBean)
        }
    } else if (data.size == 0) {
        data.add(addResourcesBean)
    }

    if (start == -1) {
        notifyDataSetChanged()
    } else {
        notifyItemRangeChanged(start, data.size)
    }
}

/**
 * LocalMedia对象转换成我们需要的ResourcesBean 并添加到Adapter体现在页面上
 */
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

/**
 * 公共配置
 */
var VIDEO_MAX_SECOND = 15//视频最大时长
var IS_LOOKER = false //是否只读
var FILE_MAX = 9//文件最大数量
var VIDEO_MAX_SIZE = 500.0F//视频最大内存 M
var PICTURE_MAX_SIZE = 5.0F//图片大小 M
var DIALOG_ACTION_LIST = arrayListOf<ChooseActionState>()
var mStateList = mutableListOf<ChooseActionState>(//弹框内容
    ChooseActionState.CHOOSE_CAMERA,
    ChooseActionState.CHOOSE_PICTURE,
    ChooseActionState.CHOOSE_VIDEO,
    ChooseActionState.CHOOSE_TAKE_PHOTO
)
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



