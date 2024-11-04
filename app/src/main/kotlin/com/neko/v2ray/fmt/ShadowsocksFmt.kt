package com.neko.v2ray.fmt

import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.dto.ProfileItem
import com.neko.v2ray.dto.V2rayConfig.OutboundBean
import com.neko.v2ray.extension.idnHost
import com.neko.v2ray.util.Utils
import java.net.URI

object ShadowsocksFmt : FmtBase() {
    fun parse(str: String): ProfileItem? {
        val config = ProfileItem.create(EConfigType.SHADOWSOCKS)

        val uri = URI(Utils.fixIllegalUrl(str))
        if (uri.idnHost.isEmpty() || uri.userInfo.isEmpty()) return null

        config.remarks = Utils.urlDecode(uri.fragment.orEmpty())
        config.server = uri.idnHost
        config.serverPort = uri.port.toString()

        val result = if (uri.userInfo.contains(":")) {
            uri.userInfo.split(":")
        } else {
            Utils.decode(uri.userInfo).split(":")
        }
        if (result.count() == 2) {
            config.method = result.first()
            config.password = result.last()
        }

        if (!uri.rawQuery.isNullOrEmpty()) {
            val queryParam = getQueryParam(uri)

            if (queryParam["plugin"] == "obfs-local" && queryParam["obfs"] == "http") {
                config.network = "tcp"
                config.headerType = "http"
                config.host = queryParam["obfs-host"]
                config.path = queryParam["path"]
            }
        }

        return config
    }

    fun toUri(config: ProfileItem): String {
        val pw = "${config.method}:${config.password}"

        return toUri(config, pw, null)
    }

    fun toOutbound(profileItem: ProfileItem): OutboundBean? {
        val outboundBean = OutboundBean.create(EConfigType.SHADOWSOCKS)

        outboundBean?.settings?.servers?.get(0)?.let { server ->
            server.address = profileItem.server.orEmpty()
            server.port = profileItem.serverPort.orEmpty().toInt()
            server.password = profileItem.password
            server.method = profileItem.method
        }

        return outboundBean
    }


}