package com.resources.uploadlib.util
import android.util.Log
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.resources.uploadlib.PICTURE_MAX_SIZE
import com.resources.uploadlib.VIDEO_MAX_SIZE
import com.resources.uploadlib.bean.ResourcesBean
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
fun PictureSelector.ChoosePicture(max:Int) {
    var sum = if(max > 9) 9 else max
    openGallery(PictureMimeType.ofImage())
        .imageEngine(GlideEngine())
        .selectionMode(PictureConfig.MULTIPLE)
        .selectionData(mediaPictureList)
        .isCamera(false)
        .maxSelectNum(sum)
        .queryMaxFileSize(PICTURE_MAX_SIZE)//设置能够选择的视频大小 单位M
        .minimumCompressSize(300)// 小于300kb的图片不压缩
        .isCompress(true)
        .forResult(PictureConfig.TYPE_IMAGE)
}

/**
 * 选择视频
 */
fun PictureSelector.ChooseVideo(max:Int,videoMaxSecond:Int) {
    var sum = if(max > 3) 3 else max
    openGallery(PictureMimeType.ofVideo())
        .imageEngine(GlideEngine())
        .selectionMode(PictureConfig.MULTIPLE)
        .selectionData(mediaVideoList)
        .maxSelectNum(sum)
        .isCamera(false)
        .queryMaxFileSize(VIDEO_MAX_SIZE)//设置能够选择的视频大小 单位M
        .maxVideoSelectNum(sum)
        .videoMaxSecond(videoMaxSecond) //视频最多30s
        .forResult(PictureConfig.TYPE_VIDEO)
}

/**
 * 拍照
 */
fun PictureSelector.TakePhoto(){
   openCamera(PictureMimeType.ofImage())
        .isCompress(true)
        .minimumCompressSize(100)
        .forResult(ResConfig.PHOTO_CODE)
}

/**
 * 获取文件并转换
 */
fun LocalMedia.LocalMedia2ResourcesBean():ResourcesBean{
    if (mimeType.contains("image")) {
        return  ResourcesBean().apply {
            this.type = ResourcesType.PICTURE
            this.state = ResourcesState.UPLOAD_ING
            this.mediaType = mimeType
            this.localPath = compressPath
            this.fileSize = "${(this@LocalMedia2ResourcesBean.size.toFloat() / 1024.0f / 1024.0f)}M"
        }
    } else if (mimeType.contains("video")) {
        return ResourcesBean().apply {
            this.type = ResourcesType.VIDEO
            this.state = ResourcesState.UPLOAD_ING
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