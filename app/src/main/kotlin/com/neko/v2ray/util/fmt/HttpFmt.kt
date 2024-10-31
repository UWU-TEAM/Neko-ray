package com.neko.v2ray.util.fmt

import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.dto.ProfileItem
import com.neko.v2ray.dto.V2rayConfig.OutboundBean
import com.neko.v2ray.extension.isNotNullEmpty
import kotlin.text.orEmpty

object HttpFmt : FmtBase() {
    fun toOutbound(profileItem: ProfileItem): OutboundBean? {
        val outboundBean = OutboundBean.create(EConfigType.HTTP)

        outboundBean?.settings?.servers?.get(0)?.let { server ->
            server.address = profileItem.server.orEmpty()
            server.port = profileItem.serverPort.orEmpty().toInt()
            if (profileItem.username.isNotNullEmpty()) {
                val socksUsersBean = OutboundBean.OutSettingsBean.ServersBean.SocksUsersBean()
                socksUsersBean.user = profileItem.username.orEmpty()
                socksUsersBean.pass = profileItem.password.orEmpty()
                server.users = listOf(socksUsersBean)
            }
        }

        return outboundBean
    }


}