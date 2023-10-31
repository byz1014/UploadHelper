package com.resources.uploadlib.bean

import android.graphics.Bitmap
import com.luck.picture.lib.entity.LocalMedia
import com.resources.uploadlib.choose.ResourcesState
import com.resources.uploadlib.choose.ResourcesType


/**
 * @Author:BYZ
 * @Time:2023/10/19 9:44
 * @blame Android Team
 * @info
 */
class ResourcesBean {
    var id:String = ""
    var type = ResourcesType.AUDIO
    var mediaType = ""
    var localPath = ""
    var httpPath = ""
    var state = ResourcesState.UPLOAD_START
    var coverImage: Bitmap?=null
    var isUpload = false
    var fileSize = "0M"
    var progress:String = "0%"
    var fileName = ""
    var fileId = ""
}


