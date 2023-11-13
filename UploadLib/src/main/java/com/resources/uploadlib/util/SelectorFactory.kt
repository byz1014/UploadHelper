package com.resources.uploadlib.util
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.PermissionUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.resources.uploadlib.bean.ResourcesBean
import com.resources.uploadlib.camera.CameraVideoActivity
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType
import java.util.*


/**
 * @Author:BYZ
 * @Time:2023/10/19 15:22
 * @blame Android Team
 * @info
 */

var mediaVideoList = mutableListOf<LocalMedia>()
var mediaPictureList = mutableListOf<LocalMedia>()

/**
 * 选择图片
 */
fun PictureSelector.ChoosePicture(max:Int,mCode:Int = PictureConfig.TYPE_IMAGE,maxSize:Float = 5f) {
    var sum = if(max > 9) 9 else max
    openGallery(PictureMimeType.ofImage())
        .imageEngine(GlideEngine())
        .selectionMode(PictureConfig.MULTIPLE)
        .selectionData(mediaPictureList)
        .isCamera(false)
        .maxSelectNum(sum)
        .queryMaxFileSize(maxSize)//设置能够选择的视频大小 单位M
        .minimumCompressSize(300)// 小于300kb的图片不压缩
        .isCompress(true)
        .forResult(mCode)
}

/**
 * 选择视频
 *
 */
fun PictureSelector.ChooseVideo(max:Int,videoMaxSecond:Int,mCode:Int = PictureConfig.TYPE_VIDEO,maxSize: Float = 260f) {
    var sum = if(max > 3) 3 else max
    openGallery(PictureMimeType.ofVideo())
        .imageEngine(GlideEngine())
        .selectionMode(PictureConfig.MULTIPLE)
        .selectionData(mediaVideoList)
        .maxSelectNum(sum)
        .isCamera(false)
        .queryMaxFileSize(maxSize)//设置能够选择的视频大小 单位M
        .maxVideoSelectNum(sum)
        .videoMaxSecond(videoMaxSecond) //视频最多30s
        .forResult(mCode)
}

/**
 * 拍照
 */
fun PictureSelector.TakePhoto(mCode:Int = ResConfig.PHOTO_CODE){
    openCamera(PictureMimeType.ofImage())
        .isCompress(true)
        .minimumCompressSize(100)
        .forResult(mCode)
}

/**
 * 获取文件并转换
 */
fun LocalMedia.LocalMedia2ResourcesBean():ResourcesBean{
    if (mimeType.contains("image")) {
        return  ResourcesBean().apply {
            this.type = ResourcesType.PICTURE
            this.state = ResourcesState.UPLOAD_START
            this.mediaType = mimeType
            this.localPath = compressPath
            this.fileSize = "${(this@LocalMedia2ResourcesBean.size.toFloat() / 1024.0f / 1024.0f)}M"
        }
    } else if (mimeType.contains("video")) {
        return ResourcesBean().apply {
            this.type = ResourcesType.VIDEO
            this.state = ResourcesState.UPLOAD_START
            this.mediaType = mimeType
            this.localPath = realPath
            this.fileSize = "${(this@LocalMedia2ResourcesBean.size.toFloat() / 1024.0f / 1024.0f)}M"
        }
    }

    return ResourcesBean()
}

fun stringToUniqueId(input: String): String {
    val uuid = UUID.nameUUIDFromBytes(input.toByteArray())
    return uuid.toString()
}

fun Activity.toCamera(maxSecond:Int, videoCode:Int){
    PermissionUtils.permission(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    ).onPermission {
        Intent(this, CameraVideoActivity::class.java).apply {
            putExtra("maxTime",maxSecond)
            startActivityForResult(this,videoCode)
        }
    }
}