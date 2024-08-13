package com.neko.v2ray.nekonet

data class Usage(
    var downloads: Long = 0L,
    var uploads: Long = 0L,
    var timeTaken: Long = 0L
)

data class DataUsage(
    var downloads: Long = 0L,
    var uploads: Long = 0L,
    var date: String = ""
)