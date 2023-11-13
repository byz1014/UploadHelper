package com.resources.uploadlib.choose


/**
 * @Author:BYZ
 * @Time:2023/10/19 9:52
 * @blame Android Team
 * @info
 */
enum class ResourcesState(state: String) {
    UPLOAD_START("开始上传"),
    UPLOAD_ING("上传中"),
    UPLOAD_SUCCESS("上传成功"),
    UPLOAD_FAIL("上传失败"),
    UPLOAD_LOGIN_FAIL("登陆信息异常"),
    UPLOAD_HTTP_FAIL("网络请求失败")


}