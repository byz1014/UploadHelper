package com.resources.uploadlib.camera

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Author:BYZ
 * @Time:2023/10/17 9:25
 * @blame Android Team
 * @info
 */

/**
 * 获取视频第一帧
 */
fun String.getFirstFrame(): Bitmap? {
    val retriever = MediaMetadataRetriever()
    var firstFrame: Bitmap? = null
    firstFrame = if(this.contains("http")){
        retriever.setDataSource(this,HashMap<String,String>())
        retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

    }else{
        retriever.setDataSource(this)
        retriever.getFrameAtTime(0)
    }
    retriever.release()
    return firstFrame
}




/**
 * 创建存储文件
 */
fun Context.createFile(format: String, extension: String): File {
//    val externalFilesDir = getExternalFilesDir("carVideo")
//    var mFile = File("/storage/emulated/0/DCIM/Camera/")
  var mBaseFile =  this.getMediaOutputDirectory("carVideo")
    return File(
        mBaseFile,
        SimpleDateFormat(
            format,
            Locale.ROOT
        ).format(System.currentTimeMillis()) + "." + extension
    )
}

fun Context.getMediaOutputDirectory(appName: String): File? {
    val appContext = applicationContext
    val files = appContext.externalMediaDirs
    var mediaDir: File? = null
    if (files != null && files.isNotEmpty()) {
        mediaDir = File(files[0], appName)
        mediaDir.mkdirs()
    }
    return if (mediaDir != null && mediaDir.exists()) {
        mediaDir
    } else {
        appContext.filesDir
    }
}
