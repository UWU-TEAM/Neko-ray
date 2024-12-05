package com.neko.v2ray.dto

data class RulesetItem(
    var remarks: String? = "",
    var ip: List<String>? = null,
    var domain: List<String>? = null,
    var outboundTag: String = "",
    var port: String? = null,
    var network: String? = null,
    var protocol: List<String>? = null,
    var enabled: Boolean = true,
    var locked: Boolean? = false,
)