package com.neko.v2ray.dto

import com.neko.v2ray.dto.V2rayConfig.OutboundBean.OutSettingsBean.FragmentBean

data class FragmentsCache(val fragmentBean: FragmentBean,
                          var ping: Long = -2)