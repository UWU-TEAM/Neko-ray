package com.neko.v2ray.util.fmt

import com.neko.v2ray.AppConfig
import com.neko.v2ray.AppConfig.LOOPBACK
import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.dto.Hysteria2Bean
import com.neko.v2ray.dto.ProfileItem
import com.neko.v2ray.dto.V2rayConfig.OutboundBean
import com.neko.v2ray.extension.idnHost
import com.neko.v2ray.extension.isNotNullEmpty
import com.neko.v2ray.util.MmkvManager.settingsStorage
import com.neko.v2ray.util.Utils
import java.net.URI

object Hysteria2Fmt : FmtBase() {
    fun parse(str: String): ProfileItem? {
        var allowInsecure = settingsStorage.decodeBool(AppConfig.PREF_ALLOW_INSECURE,false)
        val config = ProfileItem.create(EConfigType.HYSTERIA2)

        val uri = URI(Utils.fixIllegalUrl(str))
        config.remarks = Utils.urlDecode(uri.fragment.orEmpty())
        config.server = uri.idnHost
        config.serverPort = uri.port.toString()
        config.password = uri.userInfo
        config.security = AppConfig.TLS

        if (!uri.rawQuery.isNullOrEmpty()) {
            val queryParam = getQueryParam(uri)

            config.security = queryParam["security"] ?: AppConfig.TLS
            config.insecure = if (queryParam["insecure"].isNullOrEmpty()) {
                allowInsecure
            } else {
                queryParam["insecure"].orEmpty() == "1"
            }
            config.sni = queryParam["sni"]
            config.alpn = queryParam["alpn"]

            config.obfsPassword = queryParam["obfs-password"]
        }

        return config
    }

    fun toUri(config: ProfileItem): String {
        val dicQuery = HashMap<String, String>()

        config.security.let { if (it != null) dicQuery["security"] = it }
        config.sni.let { if (it.isNotNullEmpty()) dicQuery["sni"] = it.orEmpty() }
        config.alpn.let { if (it.isNotNullEmpty()) dicQuery["alpn"] = it.orEmpty() }
        config.insecure.let { dicQuery["insecure"] = if (it == true) "1" else "0" }

        if (config.obfsPassword.isNotNullEmpty()) {
            dicQuery["obfs"] = "salamander"
            dicQuery["obfs-password"] = config.obfsPassword.orEmpty()
        }

        return toUri(config, config.password, dicQuery)
    }

    fun toNativeConfig(config: ProfileItem, socksPort: Int): Hysteria2Bean? {

        val obfs = if (config.obfsPassword.isNullOrEmpty()) null else
            Hysteria2Bean.ObfsBean(
                type = "salamander",
                salamander = Hysteria2Bean.ObfsBean.SalamanderBean(
                    password = config.obfsPassword
                )
            )

        val bean = Hysteria2Bean(
            server = config.getServerAddressAndPort(),
            auth = config.password,
            obfs = obfs,
            socks5 = Hysteria2Bean.Socks5Bean(
                listen = "$LOOPBACK:${socksPort}",
            ),
            http = Hysteria2Bean.Socks5Bean(
                listen = "$LOOPBACK:${socksPort}",
            ),
            tls = Hysteria2Bean.TlsBean(
                sni = config.sni ?: config.server,
                insecure = config.insecure
            )
        )
        return bean
    }


    fun toOutbound(profileItem: ProfileItem): OutboundBean? {
        val outboundBean = OutboundBean.create(EConfigType.HYSTERIA2)
        return outboundBean
    }

}