package com.neko.v2ray.fmt

import com.neko.v2ray.AppConfig
import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.dto.NetworkType
import com.neko.v2ray.dto.ProfileItem
import com.neko.v2ray.dto.V2rayConfig.OutboundBean
import com.neko.v2ray.extension.idnHost
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.util.Utils
import java.net.URI
import kotlin.text.orEmpty

object TrojanFmt : FmtBase() {
    fun parse(str: String): ProfileItem? {
        var allowInsecure = MmkvManager.decodeSettingsBool(AppConfig.PREF_ALLOW_INSECURE, false)
        val config = ProfileItem.create(EConfigType.TROJAN)

        val uri = URI(Utils.fixIllegalUrl(str))
        config.remarks = Utils.urlDecode(uri.fragment.orEmpty())
        config.server = uri.idnHost
        config.serverPort = uri.port.toString()
        config.password = uri.userInfo

        if (uri.rawQuery.isNullOrEmpty()) {
            config.network = NetworkType.TCP.type
            config.security = AppConfig.TLS
            config.insecure = allowInsecure
        } else {
            val queryParam = getQueryParam(uri)

            getItemFormQuery(config, queryParam, allowInsecure)
            config.security = queryParam["security"] ?: AppConfig.TLS
        }

        return config
    }

    fun toUri(config: ProfileItem): String {
        val dicQuery = getQueryDic(config)

        return toUri(config, config.password, dicQuery)
    }

    fun toOutbound(profileItem: ProfileItem): OutboundBean? {
        val outboundBean = OutboundBean.create(EConfigType.TROJAN)

        outboundBean?.settings?.servers?.first()?.let { server ->
            server.address = profileItem.server.orEmpty()
            server.port = profileItem.serverPort.orEmpty().toInt()
            server.password = profileItem.password
            server.flow = profileItem.flow
        }

        outboundBean?.streamSettings?.populateTransportSettings(
            profileItem.network.orEmpty(),
            profileItem.headerType,
            profileItem.host,
            profileItem.path,
            profileItem.seed,
            profileItem.quicSecurity,
            profileItem.quicKey,
            profileItem.mode,
            profileItem.serviceName,
            profileItem.authority,
        )

        outboundBean?.streamSettings?.populateTlsSettings(
            profileItem.security.orEmpty(),
            profileItem.insecure == true,
            profileItem.sni,
            profileItem.fingerPrint,
            profileItem.alpn,
            profileItem.publicKey,
            profileItem.shortId,
            profileItem.spiderX,
        )

        return outboundBean
    }
}