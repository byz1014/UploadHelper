package com.resources.uploadlib.bean


/**
 * @Author:BYZ
 * @Time:2023/10/27 10:21
 * @blame Android Team
 * @info
 */
data class FileResultBean(
    var code: Int = -100,
    var `data`: Data ,
    var message: String,
    var responseId: String,
    var temstamp: Double
)

data class Data(
    var adjunctId: String,
    var state: String,
    var url: String
)