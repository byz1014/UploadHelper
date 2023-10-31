package com.resources.uploadlib.choose


/**
 * @Author:BYZ
 * @Time:2023/10/19 10:01
 * @blame Android Team
 * @info
 */
enum class ResourcesType(var type:Int,var typeName: String) {
    VIDEO(1,"video/*"),
    PICTURE(2,"image/*"),
    AUDIO(3,"audio/*"),
    ADD(0,"add")
}