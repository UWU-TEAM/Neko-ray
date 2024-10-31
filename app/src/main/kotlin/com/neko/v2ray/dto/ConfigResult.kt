package com.neko.v2ray.dto

data class ConfigResult(
    var status: Boolean,
    var guid: String? = null,
    var content: String = "",
    var domainPort: String? = null,
)

