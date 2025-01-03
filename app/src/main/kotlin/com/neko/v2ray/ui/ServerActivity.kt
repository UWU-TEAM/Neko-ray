package com.neko.v2ray.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.textfield.TextInputLayout
import com.neko.v2ray.AppConfig
import com.neko.v2ray.AppConfig.PREF_ALLOW_INSECURE
import com.neko.v2ray.AppConfig.WIREGUARD_LOCAL_ADDRESS_V4
import com.neko.v2ray.AppConfig.WIREGUARD_LOCAL_ADDRESS_V6
import com.neko.v2ray.AppConfig.WIREGUARD_LOCAL_MTU
import com.neko.v2ray.R
import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.dto.ServerConfig
import com.neko.v2ray.dto.V2rayConfig
import com.neko.v2ray.dto.V2rayConfig.Companion.DEFAULT_PORT
import com.neko.v2ray.dto.V2rayConfig.Companion.TLS
import com.neko.v2ray.extension.removeWhiteSpace
import com.neko.v2ray.extension.toast
import com.neko.v2ray.util.MmkvManager
import com.neko.v2ray.util.MmkvManager.settingsStorage
import com.neko.v2ray.util.SoftInputAssist
import com.neko.v2ray.util.Utils
import com.neko.v2ray.util.Utils.getIpv6Address

import com.neko.imageslider.ImageSlider
import com.neko.imageslider.constants.ActionTypes
import com.neko.imageslider.constants.AnimationTypes
import com.neko.imageslider.constants.ScaleTypes
import com.neko.imageslider.interfaces.ItemChangeListener
import com.neko.imageslider.interfaces.ItemClickListener
import com.neko.imageslider.interfaces.TouchListener
import com.neko.imageslider.models.SlideModel

class ServerActivity : BaseActivity() {

    private lateinit var softInputAssist: SoftInputAssist
    private val editGuid by lazy { intent.getStringExtra("guid").orEmpty() }
    private val isRunning by lazy {
        intent.getBooleanExtra("isRunning", false)
                && editGuid.isNotEmpty()
                && editGuid == MmkvManager.getSelectServer()
    }
    private val createConfigType by lazy {
        EConfigType.fromInt(intent.getIntExtra("createConfigType", EConfigType.VMESS.value))
            ?: EConfigType.VMESS
    }
    private val subscriptionId by lazy {
        intent.getStringExtra("subscriptionId")
    }

    private val securitys: Array<out String> by lazy {
        resources.getStringArray(R.array.securitys)
    }
    private val shadowsocksSecuritys: Array<out String> by lazy {
        resources.getStringArray(R.array.ss_securitys)
    }
    private val flows: Array<out String> by lazy {
        resources.getStringArray(R.array.flows)
    }
    private val networks: Array<out String> by lazy {
        resources.getStringArray(R.array.networks)
    }
    private val tcpTypes: Array<out String> by lazy {
        resources.getStringArray(R.array.header_type_tcp)
    }
    private val kcpAndQuicTypes: Array<out String> by lazy {
        resources.getStringArray(R.array.header_type_kcp_and_quic)
    }
    private val grpcModes: Array<out String> by lazy {
        resources.getStringArray(R.array.mode_type_grpc)
    }
    private val streamSecuritys: Array<out String> by lazy {
        resources.getStringArray(R.array.streamsecurityxs)
    }
    private val allowinsecures: Array<out String> by lazy {
        resources.getStringArray(R.array.allowinsecures)
    }
    private val uTlsItems: Array<out String> by lazy {
        resources.getStringArray(R.array.streamsecurity_utls)
    }
    private val alpns: Array<out String> by lazy {
        resources.getStringArray(R.array.streamsecurity_alpn)
    }

    // Kotlin synthetics was used, but since it is removed in 1.8. We switch to old manual approach.
    // We don't use AndroidViewBinding because, it is better to share similar logics for different
    // protocols. Use findViewById manually ensures the xml are de-coupled with the activity logic.
    private val et_remarks: EditText by lazy { findViewById(R.id.et_remarks) }
    private val et_address: EditText by lazy { findViewById(R.id.et_address) }
    private val et_port: EditText by lazy { findViewById(R.id.et_port) }
    private val et_id: EditText by lazy { findViewById(R.id.et_id) }
    private val et_alterId: EditText? by lazy { findViewById(R.id.et_alterId) }
    private val et_security: EditText? by lazy { findViewById(R.id.et_security) }
    private val sp_flow: Spinner? by lazy { findViewById(R.id.sp_flow) }
    private val sp_security: Spinner? by lazy { findViewById(R.id.sp_security) }
    private val sp_stream_security: Spinner? by lazy { findViewById(R.id.sp_stream_security) }
    private val sp_allow_insecure: Spinner? by lazy { findViewById(R.id.sp_allow_insecure) }
    private val container_allow_insecure: LinearLayout? by lazy { findViewById(R.id.lay_allow_insecure) }
    private val et_sni: EditText? by lazy { findViewById(R.id.et_sni) }
    private val container_sni: LinearLayout? by lazy { findViewById(R.id.lay_sni) }
    private val sp_stream_fingerprint: Spinner? by lazy { findViewById(R.id.sp_stream_fingerprint) } //uTLS
    private val container_fingerprint: LinearLayout? by lazy { findViewById(R.id.lay_stream_fingerprint) }
    private val sp_network: Spinner? by lazy { findViewById(R.id.sp_network) }
    private val sp_header_type: Spinner? by lazy { findViewById(R.id.sp_header_type) }
    private val sp_header_type_title: TextView? by lazy { findViewById(R.id.sp_header_type_title) }
    private val tv_request_host: TextInputLayout? by lazy { findViewById(R.id.tv_request_host) }
    private val et_request_host: EditText? by lazy { findViewById(R.id.et_request_host) }
    private val tv_path: TextInputLayout? by lazy { findViewById(R.id.tv_path) }
    private val et_path: EditText? by lazy { findViewById(R.id.et_path) }
    private val sp_stream_alpn: Spinner? by lazy { findViewById(R.id.sp_stream_alpn) } //uTLS
    private val container_alpn: LinearLayout? by lazy { findViewById(R.id.lay_stream_alpn) }
    private val et_public_key: EditText? by lazy { findViewById(R.id.et_public_key) }
    private val container_public_key: LinearLayout? by lazy { findViewById(R.id.lay_public_key) }
    private val et_short_id: EditText? by lazy { findViewById(R.id.et_short_id) }
    private val container_short_id: LinearLayout? by lazy { findViewById(R.id.lay_short_id) }
    private val et_spider_x: EditText? by lazy { findViewById(R.id.et_spider_x) }
    private val container_spider_x: LinearLayout? by lazy { findViewById(R.id.lay_spider_x) }
    private val et_reserved1: EditText? by lazy { findViewById(R.id.et_reserved1) }
    private val et_reserved2: EditText? by lazy { findViewById(R.id.et_reserved2) }
    private val et_reserved3: EditText? by lazy { findViewById(R.id.et_reserved3) }
    private val et_local_address: EditText? by lazy { findViewById(R.id.et_local_address) }
    private val et_local_mtu: EditText? by lazy { findViewById(R.id.et_local_mtu) }
    private val et_obfs_password: EditText? by lazy { findViewById(R.id.et_obfs_password) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = MmkvManager.decodeServerConfig(editGuid)
        when (config?.configType ?: createConfigType) {
            EConfigType.VMESS -> setContentView(R.layout.activity_server_vmess)
            EConfigType.CUSTOM -> return
            EConfigType.SHADOWSOCKS -> setContentView(R.layout.activity_server_shadowsocks)
            EConfigType.SOCKS -> setContentView(R.layout.activity_server_socks)
            EConfigType.HTTP -> setContentView(R.layout.activity_server_socks)
            EConfigType.VLESS -> setContentView(R.layout.activity_server_vless)
            EConfigType.TROJAN -> setContentView(R.layout.activity_server_trojan)
            EConfigType.WIREGUARD -> setContentView(R.layout.activity_server_wireguard)
            EConfigType.HYSTERIA2 -> setContentView(R.layout.activity_server_hysteria2)
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sp_network?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val types = transportTypes(networks[position])
                sp_header_type?.isEnabled = types.size > 1
                val adapter =
                    ArrayAdapter(this@ServerActivity, android.R.layout.simple_spinner_item, types)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp_header_type?.adapter = adapter
                sp_header_type_title?.text = if (networks[position] == "grpc")
                    getString(R.string.server_lab_mode_type) else
                    getString(R.string.server_lab_head_type)
                config?.getProxyOutbound()?.getTransportSettingDetails()?.let { transportDetails ->
                    sp_header_type?.setSelection(Utils.arrayFind(types, transportDetails[0]))
                    et_request_host?.text = Utils.getEditable(transportDetails[1])
                    et_path?.text = Utils.getEditable(transportDetails[2])
                }

                tv_request_host?.hint = Utils.getEditable(
                    getString(
                        when (networks[position]) {
                            "tcp" -> R.string.server_lab_request_host_http
                            "ws" -> R.string.server_lab_request_host_ws
                            "httpupgrade" -> R.string.server_lab_request_host_httpupgrade
                            "splithttp" -> R.string.server_lab_request_host_splithttp
                            "h2" -> R.string.server_lab_request_host_h2
                            "quic" -> R.string.server_lab_request_host_quic
                            "grpc" -> R.string.server_lab_request_host_grpc
                            else -> R.string.server_lab_request_host
                        }
                    )
                )

                tv_path?.hint = Utils.getEditable(
                    getString(
                        when (networks[position]) {
                            "kcp" -> R.string.server_lab_path_kcp
                            "ws" -> R.string.server_lab_path_ws
                            "httpupgrade" -> R.string.server_lab_path_httpupgrade
                            "splithttp" -> R.string.server_lab_path_splithttp
                            "h2" -> R.string.server_lab_path_h2
                            "quic" -> R.string.server_lab_path_quic
                            "grpc" -> R.string.server_lab_path_grpc
                            else -> R.string.server_lab_path
                        }
                    )
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }
        sp_stream_security?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val isBlank = streamSecuritys[position].isBlank()
                val isTLS = streamSecuritys[position] == TLS

                when {
                    // Case 1: Null or blank
                    isBlank -> {
                        listOf(
                            container_sni, container_fingerprint, container_alpn,
                            container_allow_insecure, container_public_key,
                            container_short_id, container_spider_x
                        ).forEach { it?.visibility = View.GONE }
                    }

                    // Case 2: TLS value
                    isTLS -> {
                        listOf(
                            container_sni,
                            container_fingerprint,
                            container_alpn
                        ).forEach { it?.visibility = View.VISIBLE }
                        container_allow_insecure?.visibility = View.VISIBLE
                        listOf(
                            container_public_key,
                            container_short_id,
                            container_spider_x
                        ).forEach { it?.visibility = View.GONE }
                    }

                    // Case 3: Other reality values
                    else -> {
                        listOf(container_sni, container_fingerprint).forEach {
                            it?.visibility = View.VISIBLE
                        }
                        container_alpn?.visibility = View.GONE
                        container_allow_insecure?.visibility = View.GONE
                        listOf(
                            container_public_key,
                            container_short_id,
                            container_spider_x
                        ).forEach { it?.visibility = View.VISIBLE }
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // do nothing
            }
        }
        if (config != null) {
            bindingServer(config)
        } else {
            clearServer()
        }
        softInputAssist = SoftInputAssist(this)

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider) // init imageSlider
        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel(R.drawable.uwu_banner_image_3, ""))
        imageList.add(SlideModel(R.drawable.uwu_banner_image_5, ""))
        imageList.add(SlideModel(R.drawable.uwu_banner_image_6, ""))
        imageList.add(SlideModel(R.drawable.uwu_banner_image_7, ""))
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                // You can listen here.
                println("normal")
            }
            override fun doubleClick(position: Int) {
                // Do not use onItemSelected if you are using a double click listener at the same time.
                // Its just added for specific cases.
                // Listen for clicks under 250 milliseconds.
                println("its double")
            }
        })
        imageSlider.setItemChangeListener(object : ItemChangeListener {
            override fun onItemChanged(position: Int) {
                //println("Pos: " + position)
            }
        })
        imageSlider.setTouchListener(object : TouchListener {
            override fun onTouched(touched: ActionTypes, position: Int) {
                if (touched == ActionTypes.DOWN){
                    imageSlider.stopSliding()
                } else if (touched == ActionTypes.UP ) {
                    imageSlider.startSliding(3000)
                }
            }
        })
    }

    /**
     * binding selected server config
     */
    private fun bindingServer(config: ServerConfig): Boolean {
        val outbound = config.getProxyOutbound() ?: return false

        et_remarks.text = Utils.getEditable(config.remarks)
        et_address.text = Utils.getEditable(outbound.getServerAddress().orEmpty())
        et_port.text =
            Utils.getEditable(outbound.getServerPort()?.toString() ?: DEFAULT_PORT.toString())
        et_id.text = Utils.getEditable(outbound.getPassword().orEmpty())
        et_alterId?.text =
            Utils.getEditable(outbound.settings?.vnext?.get(0)?.users?.get(0)?.alterId.toString())
        if (config.configType == EConfigType.SOCKS
            || config.configType == EConfigType.HTTP
        ) {
            et_security?.text =
                Utils.getEditable(outbound.settings?.servers?.get(0)?.users?.get(0)?.user.orEmpty())
        } else if (config.configType == EConfigType.VLESS) {
            et_security?.text = Utils.getEditable(outbound.getSecurityEncryption().orEmpty())
            val flow = Utils.arrayFind(
                flows,
                outbound.settings?.vnext?.get(0)?.users?.get(0)?.flow.orEmpty()
            )
            if (flow >= 0) {
                sp_flow?.setSelection(flow)
            }
        } else if (config.configType == EConfigType.WIREGUARD) {
            et_public_key?.text =
                Utils.getEditable(outbound.settings?.peers?.get(0)?.publicKey.orEmpty())
            if (outbound.settings?.reserved == null) {
                et_reserved1?.text = Utils.getEditable("0")
                et_reserved2?.text = Utils.getEditable("0")
                et_reserved3?.text = Utils.getEditable("0")
            } else {
                et_reserved1?.text =
                    Utils.getEditable(outbound.settings?.reserved?.get(0).toString())
                et_reserved2?.text =
                    Utils.getEditable(outbound.settings?.reserved?.get(1).toString())
                et_reserved3?.text =
                    Utils.getEditable(outbound.settings?.reserved?.get(2).toString())
            }
            if (outbound.settings?.address == null) {
                et_local_address?.text =
                    Utils.getEditable("${WIREGUARD_LOCAL_ADDRESS_V4},${WIREGUARD_LOCAL_ADDRESS_V6}")
            } else {
                val list = outbound.settings?.address as List<*>
                et_local_address?.text = Utils.getEditable(list.joinToString(","))
            }
            if (outbound.settings?.mtu == null) {
                et_local_mtu?.text = Utils.getEditable(WIREGUARD_LOCAL_MTU)
            } else {
                et_local_mtu?.text = Utils.getEditable(outbound.settings?.mtu.toString())
            }
        } else if (config.configType == EConfigType.HYSTERIA2) {
            et_obfs_password?.text = Utils.getEditable(outbound.settings?.obfsPassword)
        }

        val securityEncryptions =
            if (config.configType == EConfigType.SHADOWSOCKS) shadowsocksSecuritys else securitys
        val security =
            Utils.arrayFind(securityEncryptions, outbound.getSecurityEncryption().orEmpty())
        if (security >= 0) {
            sp_security?.setSelection(security)
        }

        val streamSetting = config.outboundBean?.streamSettings ?: return true
        val streamSecurity = Utils.arrayFind(streamSecuritys, streamSetting.security)
        if (streamSecurity >= 0) {
            sp_stream_security?.setSelection(streamSecurity)
            (streamSetting.tlsSettings ?: streamSetting.realitySettings)?.let { tlsSetting ->
                container_sni?.visibility = View.VISIBLE
                container_fingerprint?.visibility = View.VISIBLE
                container_alpn?.visibility = View.VISIBLE
                et_sni?.text = Utils.getEditable(tlsSetting.serverName)
                tlsSetting.fingerprint?.let {
                    val utlsIndex = Utils.arrayFind(uTlsItems, tlsSetting.fingerprint)
                    sp_stream_fingerprint?.setSelection(utlsIndex)
                }
                tlsSetting.alpn?.let {
                    val alpnIndex = Utils.arrayFind(
                        alpns,
                        Utils.removeWhiteSpace(tlsSetting.alpn.joinToString(",")).orEmpty()
                    )
                    sp_stream_alpn?.setSelection(alpnIndex)
                }
                if (streamSetting.tlsSettings != null) {
                    container_allow_insecure?.visibility = View.VISIBLE
                    val allowinsecure =
                        Utils.arrayFind(allowinsecures, tlsSetting.allowInsecure.toString())
                    if (allowinsecure >= 0) {
                        sp_allow_insecure?.setSelection(allowinsecure)
                    }
                    container_public_key?.visibility = View.GONE
                    container_short_id?.visibility = View.GONE
                    container_spider_x?.visibility = View.GONE
                } else { // reality settings
                    container_public_key?.visibility = View.VISIBLE
                    et_public_key?.text = Utils.getEditable(tlsSetting.publicKey.orEmpty())
                    container_short_id?.visibility = View.VISIBLE
                    et_short_id?.text = Utils.getEditable(tlsSetting.shortId.orEmpty())
                    container_spider_x?.visibility = View.VISIBLE
                    et_spider_x?.text = Utils.getEditable(tlsSetting.spiderX.orEmpty())
                    container_allow_insecure?.visibility = View.GONE
                }
            }
            if (streamSetting.tlsSettings == null && streamSetting.realitySettings == null) {
                container_sni?.visibility = View.GONE
                container_fingerprint?.visibility = View.GONE
                container_alpn?.visibility = View.GONE
                container_allow_insecure?.visibility = View.GONE
                container_public_key?.visibility = View.GONE
                container_short_id?.visibility = View.GONE
                container_spider_x?.visibility = View.GONE
            }
        }
        val network = Utils.arrayFind(networks, streamSetting.network)
        if (network >= 0) {
            sp_network?.setSelection(network)
        }
        return true
    }

    /**
     * clear or init server config
     */
    private fun clearServer(): Boolean {
        et_remarks.text = null
        et_address.text = null
        et_port.text = Utils.getEditable(DEFAULT_PORT.toString())
        et_id.text = null
        et_alterId?.text = Utils.getEditable("0")
        sp_security?.setSelection(0)
        sp_network?.setSelection(0)

        sp_header_type?.setSelection(0)
        et_request_host?.text = null
        et_path?.text = null
        sp_stream_security?.setSelection(0)
        sp_allow_insecure?.setSelection(0)
        et_sni?.text = null

        //et_security.text = null
        sp_flow?.setSelection(0)
        et_public_key?.text = null
        et_reserved1?.text = Utils.getEditable("0")
        et_reserved2?.text = Utils.getEditable("0")
        et_reserved3?.text = Utils.getEditable("0")
        et_local_address?.text =
            Utils.getEditable("${WIREGUARD_LOCAL_ADDRESS_V4},${WIREGUARD_LOCAL_ADDRESS_V6}")
        et_local_mtu?.text = Utils.getEditable(WIREGUARD_LOCAL_MTU)
        return true
    }

    /**
     * save server config
     */
    private fun saveServer(): Boolean {
        if (TextUtils.isEmpty(et_remarks.text.toString())) {
            toast(R.string.server_lab_remarks)
            return false
        }
        if (TextUtils.isEmpty(et_address.text.toString())) {
            toast(R.string.server_lab_address)
            return false
        }
        val port = Utils.parseInt(et_port.text.toString())
        if (port <= 0) {
            toast(R.string.server_lab_port)
            return false
        }
        val config =
            MmkvManager.decodeServerConfig(editGuid) ?: ServerConfig.create(createConfigType)
        if (config.configType != EConfigType.SOCKS
            && config.configType != EConfigType.HTTP
            && TextUtils.isEmpty(et_id.text.toString())
        ) {
            if (config.configType == EConfigType.TROJAN
                || config.configType == EConfigType.SHADOWSOCKS
                || config.configType == EConfigType.HYSTERIA2
            ) {
                toast(R.string.server_lab_id3)
            } else {
                toast(R.string.server_lab_id)
            }
            return false
        }
        sp_stream_security?.let {
            if (config.configType == EConfigType.TROJAN && TextUtils.isEmpty(streamSecuritys[it.selectedItemPosition])) {
                toast(R.string.server_lab_stream_security)
                return false
            }
        }
        et_alterId?.let {
            val alterId = Utils.parseInt(it.text.toString())
            if (alterId < 0) {
                toast(R.string.server_lab_alterid)
                return false
            }
        }

        config.remarks = et_remarks.text.toString().trim()
        config.outboundBean?.settings?.vnext?.get(0)?.let { vnext ->
            saveVnext(vnext, port, config)
        }
        config.outboundBean?.settings?.servers?.get(0)?.let { server ->
            saveServers(server, port, config)
        }
        val wireguard = config.outboundBean?.settings
        wireguard?.peers?.get(0)?.let { _ ->
            savePeer(wireguard, port)
        }

        config.outboundBean?.streamSettings?.let {
            val sni = saveStreamSettings(it)
            saveTls(it, sni)
        }
        if (config.subscriptionId.isEmpty() && !subscriptionId.isNullOrEmpty()) {
            config.subscriptionId = subscriptionId.orEmpty()
        }
        if (config.configType == EConfigType.HYSTERIA2) {
            config.outboundBean?.settings?.obfsPassword = et_obfs_password?.text?.toString()
        }

        MmkvManager.encodeServerConfig(editGuid, config)
        toast(R.string.toast_success)
        finish()
        return true
    }

    private fun saveVnext(
        vnext: V2rayConfig.OutboundBean.OutSettingsBean.VnextBean,
        port: Int,
        config: ServerConfig
    ) {
        vnext.address = et_address.text.toString().trim()
        vnext.port = port
        vnext.users[0].id = et_id.text.toString().trim()
        if (config.configType == EConfigType.VMESS) {
            vnext.users[0].alterId = Utils.parseInt(et_alterId?.text.toString())
            vnext.users[0].security = securitys[sp_security?.selectedItemPosition ?: 0]
        } else if (config.configType == EConfigType.VLESS) {
            vnext.users[0].encryption = et_security?.text.toString().trim()
            vnext.users[0].flow = flows[sp_flow?.selectedItemPosition ?: 0]
        }
    }

    private fun saveServers(
        server: V2rayConfig.OutboundBean.OutSettingsBean.ServersBean,
        port: Int,
        config: ServerConfig
    ) {
        server.address = et_address.text.toString().trim()
        server.port = port
        if (config.configType == EConfigType.SHADOWSOCKS) {
            server.password = et_id.text.toString().trim()
            server.method = shadowsocksSecuritys[sp_security?.selectedItemPosition ?: 0]
        } else if (config.configType == EConfigType.SOCKS || config.configType == EConfigType.HTTP) {
            if (TextUtils.isEmpty(et_security?.text) && TextUtils.isEmpty(et_id.text)) {
                server.users = null
            } else {
                val socksUsersBean =
                    V2rayConfig.OutboundBean.OutSettingsBean.ServersBean.SocksUsersBean()
                socksUsersBean.user = et_security?.text.toString().trim()
                socksUsersBean.pass = et_id.text.toString().trim()
                server.users = listOf(socksUsersBean)
            }
        } else if (config.configType == EConfigType.TROJAN || config.configType == EConfigType.HYSTERIA2) {
            server.password = et_id.text.toString().trim()
        }
    }

    private fun savePeer(wireguard: V2rayConfig.OutboundBean.OutSettingsBean, port: Int) {
        wireguard.secretKey = et_id.text.toString().trim()
        wireguard.peers?.get(0)?.publicKey = et_public_key?.text.toString().trim()
        wireguard.peers?.get(0)?.endpoint =
            getIpv6Address(et_address.text.toString().trim()) + ":" + port
        val reserved1 = Utils.parseInt(et_reserved1?.text.toString())
        val reserved2 = Utils.parseInt(et_reserved2?.text.toString())
        val reserved3 = Utils.parseInt(et_reserved3?.text.toString())
        if (reserved1 > 0 || reserved2 > 0 || reserved3 > 0) {
            wireguard.reserved = listOf(reserved1, reserved2, reserved3)
        } else {
            wireguard.reserved = null
        }
        wireguard.address = et_local_address?.text.toString().removeWhiteSpace().split(",")
        wireguard.mtu = Utils.parseInt(et_local_mtu?.text.toString())
    }

    private fun saveStreamSettings(streamSetting: V2rayConfig.OutboundBean.StreamSettingsBean): String? {
        val network = sp_network?.selectedItemPosition ?: return null
        val type = sp_header_type?.selectedItemPosition ?: return null
        val requestHost = et_request_host?.text?.toString()?.trim() ?: return null
        val path = et_path?.text?.toString()?.trim() ?: return null

        val sni = streamSetting.populateTransportSettings(
            transport = networks[network],
            headerType = transportTypes(networks[network])[type],
            host = requestHost,
            path = path,
            seed = path,
            quicSecurity = requestHost,
            key = path,
            mode = transportTypes(networks[network])[type],
            serviceName = path,
            authority = requestHost,
        )

        return sni
    }

    private fun saveTls(streamSetting: V2rayConfig.OutboundBean.StreamSettingsBean, sni: String?) {
        val streamSecurity = sp_stream_security?.selectedItemPosition ?: return
        val sniField = et_sni?.text?.toString()?.trim()
        val allowInsecureField = sp_allow_insecure?.selectedItemPosition
        val utlsIndex = sp_stream_fingerprint?.selectedItemPosition ?: 0
        val alpnIndex = sp_stream_alpn?.selectedItemPosition ?: 0
        val publicKey = et_public_key?.text?.toString()
        val shortId = et_short_id?.text?.toString()
        val spiderX = et_spider_x?.text?.toString()

        val allowInsecure =
            if (allowInsecureField == null || allowinsecures[allowInsecureField].isBlank()) {
                settingsStorage?.decodeBool(PREF_ALLOW_INSECURE) ?: false
            } else {
                allowinsecures[allowInsecureField].toBoolean()
            }

        streamSetting.populateTlsSettings(
            streamSecurity = streamSecuritys[streamSecurity],
            allowInsecure = allowInsecure,
            sni = sniField ?: sni ?: "",
            fingerprint = uTlsItems[utlsIndex],
            alpns = alpns[alpnIndex],
            publicKey = publicKey,
            shortId = shortId,
            spiderX = spiderX
        )
    }

    private fun transportTypes(network: String?): Array<out String> {
        return when (network) {
            "tcp" -> {
                tcpTypes
            }

            "kcp", "quic" -> {
                kcpAndQuicTypes
            }

            "grpc" -> {
                grpcModes
            }

            else -> {
                arrayOf("---")
            }
        }
    }

    /**
     * save server config
     */
    private fun deleteServer(): Boolean {
        if (editGuid.isNotEmpty()) {
            if (editGuid != MmkvManager.getSelectServer()) {
                if (settingsStorage?.decodeBool(AppConfig.PREF_CONFIRM_REMOVE) == true) {
                    MaterialAlertDialogBuilder(this).setMessage(R.string.del_config_comfirm)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            MmkvManager.removeServer(editGuid)
                            finish()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            // do nothing
                        }
                        .show()
                } else {
                    MmkvManager.removeServer(editGuid)
                    finish()
                }
            } else {
                application.toast(R.string.toast_action_not_allowed)
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_server, menu)
        val delButton = menu.findItem(R.id.del_config)
        val saveButton = menu.findItem(R.id.save_config)

        if (editGuid.isNotEmpty()) {
            if (isRunning) {
                delButton?.isVisible = false
                saveButton?.isVisible = false
            }
        } else {
            delButton?.isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.del_config -> {
            deleteServer()
            true
        }

        R.id.save_config -> {
            saveServer()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()
    }
}
