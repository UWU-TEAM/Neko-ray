package com.neko.v2ray.util

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import com.neko.v2ray.AppConfig
import com.neko.v2ray.R
import com.neko.v2ray.dto.*
import com.neko.v2ray.util.MmkvManager.settingsStorage
import com.neko.v2ray.util.fmt.Hysteria2Fmt
import com.neko.v2ray.util.fmt.ShadowsocksFmt
import com.neko.v2ray.util.fmt.SocksFmt
import com.neko.v2ray.util.fmt.TrojanFmt
import com.neko.v2ray.util.fmt.VlessFmt
import com.neko.v2ray.util.fmt.VmessFmt
import com.neko.v2ray.util.fmt.WireguardFmt
import java.lang.reflect.Type
import java.net.URI
import java.util.*

object AngConfigManager {
    /**
     * parse config form qrcode or...
     */
    private fun parseConfig(
        str: String?,
        subid: String,
        subItem: SubscriptionItem?,
        removedSelectedServer: ServerConfig?
    ): Int {
        try {
            if (str == null || TextUtils.isEmpty(str)) {
                return R.string.toast_none_data
            }
            val config = if (str.startsWith(EConfigType.VMESS.protocolScheme)) {
                VmessFmt.parseVmess(str)
            } else if (str.startsWith(EConfigType.SHADOWSOCKS.protocolScheme)) {
                ShadowsocksFmt.parseShadowsocks(str)
            } else if (str.startsWith(EConfigType.SOCKS.protocolScheme)) {
                SocksFmt.parseSocks(str)
            } else if (str.startsWith(EConfigType.TROJAN.protocolScheme)) {
                TrojanFmt.parseTrojan(str)
            } else if (str.startsWith(EConfigType.VLESS.protocolScheme)) {
                VlessFmt.parseVless(str)
            } else if (str.startsWith(EConfigType.WIREGUARD.protocolScheme)) {
                WireguardFmt.parseWireguard(str)
            } else if (str.startsWith(EConfigType.HYSTERIA2.protocolScheme)) {
                Hysteria2Fmt.parseHysteria2(str)
            } else {
                null
            }

            if (config == null) {
                return R.string.toast_incorrect_protocol
            }
            //filter
            if (subItem?.filter != null && subItem.filter?.isNotEmpty() == true && config.remarks.isNotEmpty()) {
                val matched = Regex(pattern = subItem.filter ?: "")
                    .containsMatchIn(input = config.remarks)
                if (!matched) return -1
            }

            config.subscriptionId = subid
            val guid = MmkvManager.encodeServerConfig("", config)
            if (removedSelectedServer != null &&
                config.getProxyOutbound()
                    ?.getServerAddress() == removedSelectedServer.getProxyOutbound()
                    ?.getServerAddress() &&
                config.getProxyOutbound()
                    ?.getServerPort() == removedSelectedServer.getProxyOutbound()
                    ?.getServerPort()
            ) {
                MmkvManager.setSelectServer(guid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
        return 0
    }

    /**
     * share config
     */
    private fun shareConfig(guid: String): String {
        try {
            val config = MmkvManager.decodeServerConfig(guid) ?: return ""

            return config.configType.protocolScheme + when (config.configType) {
                EConfigType.VMESS -> VmessFmt.toUri(config)
                EConfigType.CUSTOM -> ""
                EConfigType.SHADOWSOCKS -> ShadowsocksFmt.toUri(config)
                EConfigType.SOCKS -> SocksFmt.toUri(config)
                EConfigType.HTTP -> ""
                EConfigType.VLESS -> VlessFmt.toUri(config)
                EConfigType.TROJAN -> TrojanFmt.toUri(config)
                EConfigType.WIREGUARD -> WireguardFmt.toUri(config)
                EConfigType.HYSTERIA2 -> Hysteria2Fmt.toUri(config)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * share2Clipboard
     */
    fun share2Clipboard(context: Context, guid: String): Int {
        try {
            val conf = shareConfig(guid)
            if (TextUtils.isEmpty(conf)) {
                return -1
            }

            Utils.setClipboard(context, conf)

        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
        return 0
    }

    /**
     * share2Clipboard
     */
    fun shareNonCustomConfigsToClipboard(context: Context, serverList: List<String>): Int {
        try {
            val sb = StringBuilder()
            for (guid in serverList) {
                val url = shareConfig(guid)
                if (TextUtils.isEmpty(url)) {
                    continue
                }
                sb.append(url)
                sb.appendLine()
            }
            if (sb.count() > 0) {
                Utils.setClipboard(context, sb.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
        return 0
    }

    /**
     * share2QRCode
     */
    fun share2QRCode(guid: String): Bitmap? {
        try {
            val conf = shareConfig(guid)
            if (TextUtils.isEmpty(conf)) {
                return null
            }
            return QRCodeDecoder.createQRCode(conf)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * shareFullContent2Clipboard
     */
    fun shareFullContent2Clipboard(context: Context, guid: String?): Int {
        try {
            if (guid == null) return -1
            val result = V2rayConfigUtil.getV2rayConfig(context, guid)
            if (result.status) {
                Utils.setClipboard(context, result.content)
            } else {
                return -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
        return 0
    }

    fun importBatchConfig(server: String?, subid: String, append: Boolean): Pair<Int, Int> {
        var count = parseBatchConfig(Utils.decode(server), subid, append)
        if (count <= 0) {
            count = parseBatchConfig(server, subid, append)
        }
        if (count <= 0) {
            count = parseCustomConfigServer(server, subid)
        }

        var countSub = parseBatchSubscription(server)
        if (countSub <= 0) {
            countSub = parseBatchSubscription(Utils.decode(server))
        }
        if (countSub > 0) {
            updateConfigViaSubAll()
        }

        return count to countSub
    }

    fun parseBatchSubscription(servers: String?): Int {
        try {
            if (servers == null) {
                return 0
            }

            var count = 0
            servers.lines()
                .forEach { str ->
                    if (str.startsWith(AppConfig.PROTOCOL_HTTP) || str.startsWith(AppConfig.PROTOCOL_HTTPS)) {
                        count += importUrlAsSubscription(str)
                    }
                }
            return count
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun parseBatchConfig(servers: String?, subid: String, append: Boolean): Int {
        try {
            if (servers == null) {
                return 0
            }
            val removedSelectedServer =
                if (!TextUtils.isEmpty(subid) && !append) {
                    MmkvManager.decodeServerConfig(
                        MmkvManager.getSelectServer().orEmpty()
                    )?.let {
                        if (it.subscriptionId == subid) {
                            return@let it
                        }
                        return@let null
                    }
                } else {
                    null
                }
            if (!append) {
                MmkvManager.removeServerViaSubid(subid)
            }

            val subItem = MmkvManager.decodeSubscription(subid)
            var count = 0
            servers.lines()
                .reversed()
                .forEach {
                    val resId = parseConfig(it, subid, subItem, removedSelectedServer)
                    if (resId == 0) {
                        count++
                    }
                }
            return count
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun parseCustomConfigServer(server: String?, subid: String): Int {
        if (server == null) {
            return 0
        }
        if (server.contains("inbounds")
            && server.contains("outbounds")
            && server.contains("routing")
        ) {
            try {
                //val gson = GsonBuilder().setPrettyPrinting().create()
                val gson = GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeAdapter( // custom serialiser is needed here since JSON by default parse number as Double, core will fail to start
                        object : TypeToken<Double>() {}.type,
                        JsonSerializer { src: Double?, _: Type?, _: JsonSerializationContext? ->
                            JsonPrimitive(
                                src?.toInt()
                            )
                        }
                    )
                    .create()
                val serverList: Array<Any> =
                    Gson().fromJson(server, Array<Any>::class.java)

                if (serverList.isNotEmpty()) {
                    var count = 0
                    for (srv in serverList.reversed()) {
                        val config = ServerConfig.create(EConfigType.CUSTOM)
                        config.fullConfig =
                            Gson().fromJson(Gson().toJson(srv), V2rayConfig::class.java)
                        config.remarks = config.fullConfig?.remarks
                            ?: ("%04d-".format(count + 1) + System.currentTimeMillis()
                                .toString())
                        config.subscriptionId = subid
                        val key = MmkvManager.encodeServerConfig("", config)
                        MmkvManager.encodeServerRaw(key, gson.toJson(srv))
                        count += 1
                    }
                    return count
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // For compatibility
            val config = ServerConfig.create(EConfigType.CUSTOM)
            config.subscriptionId = subid
            config.fullConfig = Gson().fromJson(server, V2rayConfig::class.java)
            config.remarks = config.fullConfig?.remarks ?: System.currentTimeMillis().toString()
            val key = MmkvManager.encodeServerConfig("", config)
            MmkvManager.encodeServerRaw(key, server)
            return 1
        } else if (server.startsWith("[Interface]") && server.contains("[Peer]")) {
            val config = WireguardFmt.parseWireguardConfFile(server)
                ?: return R.string.toast_incorrect_protocol
            config.fullConfig?.remarks ?: System.currentTimeMillis().toString()
            val key = MmkvManager.encodeServerConfig("", config)
            MmkvManager.encodeServerRaw(key, server)
            return 1
        } else {
            return 0
        }
    }

    fun updateConfigViaSubAll(): Int {
        var count = 0
        try {
            MmkvManager.decodeSubscriptions().forEach {
                count += updateConfigViaSub(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
        return count
    }

    fun updateConfigViaSub(it: Pair<String, SubscriptionItem>): Int {
        try {
            if (TextUtils.isEmpty(it.first)
                || TextUtils.isEmpty(it.second.remarks)
                || TextUtils.isEmpty(it.second.url)
            ) {
                return 0
            }
            if (!it.second.enabled) {
                return 0
            }
            val url = Utils.idnToASCII(it.second.url)
            if (!Utils.isValidUrl(url)) {
                return 0
            }
            Log.d(AppConfig.ANG_PACKAGE, url)
            var configText = try {
                Utils.getUrlContentWithCustomUserAgent(url)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
            if (configText.isEmpty()) {
                configText = try {
                    val httpPort = Utils.parseInt(
                        settingsStorage?.decodeString(AppConfig.PREF_HTTP_PORT),
                        AppConfig.PORT_HTTP.toInt()
                    )
                    Utils.getUrlContentWithCustomUserAgent(url, 30000, httpPort)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            }
            if (configText.isEmpty()) {
                return 0
            }
            return parseConfigViaSub(configText, it.first, false)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    private fun parseConfigViaSub(server: String?, subid: String, append: Boolean): Int {
        var count = parseBatchConfig(Utils.decode(server), subid, append)
        if (count <= 0) {
            count = parseBatchConfig(server, subid, append)
        }
        if (count <= 0) {
            count = parseCustomConfigServer(server, subid)
        }
        return count
    }

    private fun importUrlAsSubscription(url: String): Int {
        val subscriptions = MmkvManager.decodeSubscriptions()
        subscriptions.forEach {
            if (it.second.url == url) {
                return 0
            }
        }
        val uri = URI(Utils.fixIllegalUrl(url))
        val subItem = SubscriptionItem()
        subItem.remarks = uri.fragment ?: "import sub"
        subItem.url = url
        MmkvManager.encodeSubscription("", subItem)
        return 1
    }
}
