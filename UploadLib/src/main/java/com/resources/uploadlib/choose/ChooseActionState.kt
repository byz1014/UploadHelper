package com.resources.uploadlib.choose

import com.resources.uploadlib.PICTURE_MAX_SIZE
import com.resources.uploadlib.VIDEO_MAX_SIZE


/**
 * @Author:BYZ
 * @Time:2023/10/19 14:09
 * @blame Android Team
 * @info
 */
enum class ChooseActionState(var title: String, var describe: String, var actionType: Int) {
    CHOOSE_VIDEO("选择视频", "单次选择最多3张，视频大小最大为${VIDEO_MAX_SIZE.toInt()}M", 0),
    CHOOSE_PICTURE("选择图片", "单次选择最多9张，图片最大为${PICTURE_MAX_SIZE.toInt()}M", 1),
    CHOOSE_PICTURE_AND_VIDEO("选择视频和图片", "视频最大${VIDEO_MAX_SIZE.toInt()}M，图片最大${PICTURE_MAX_SIZE.toInt()}M", 2),
    CHOOSE_TAKE_PHOTO("拍摄图片", "单张照片最大${PICTURE_MAX_SIZE.toInt()}M", 3),
    CHOOSE_CAMERA("拍摄视频", "单个视频最大${VIDEO_MAX_SIZE.toInt()}M", 4)


}