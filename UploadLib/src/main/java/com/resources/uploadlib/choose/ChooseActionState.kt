package com.resources.uploadlib.choose

import com.luck.picture.lib.config.PictureConfig
import com.resources.uploadlib.util.ResConfig


/**
 * @Author:BYZ
 * @Time:2023/10/19 14:09
 * @blame Android Team
 * @info
 */
enum class ChooseActionState(var title: String, var describe: String, var actionCode: Int) {
    CHOOSE_VIDEO("选择视频", "单次选择最多3张，视频大小最大为%sM", PictureConfig.TYPE_VIDEO),
    CHOOSE_PICTURE("选择图片", "单次选择最多9张，图片最大为%sM", PictureConfig.TYPE_IMAGE),
    CHOOSE_PICTURE_AND_VIDEO("选择视频和图片", "视频最大%sM，图片最大%sM", 2),
    CHOOSE_TAKE_PHOTO("拍摄图片", "单张照片最大%sM", ResConfig.PHOTO_CODE),
    CHOOSE_CAMERA("拍摄视频", "单个视频最大%sM", ResConfig.CAMERA_CODE)
}