package com.resources.uploadlib.bean

import com.luck.picture.lib.config.PictureConfig
import com.resources.uploadlib.util.ResConfig


/**
 * @Author:BYZ
 * @Time:2023/11/1 14:34
 * @blame Android Team
 * @info
 */
class RequestCodeBean {
    var mVideoCode = ResConfig.CAMERA_CODE
    var mPictureCode = ResConfig.PHOTO_CODE
    var mChoosePictureCode = PictureConfig.TYPE_IMAGE
    var mChooseVideoCode = PictureConfig.TYPE_VIDEO
}