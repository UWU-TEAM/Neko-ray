package com.neko.v2ray.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.neko.v2ray.AppConfig.MSG_MEASURE_CONFIG
import com.neko.v2ray.AppConfig.MSG_MEASURE_CONFIG_CANCEL
import com.neko.v2ray.AppConfig.MSG_MEASURE_CONFIG_SUCCESS
import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.extension.serializable
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.handler.SpeedtestManager
import com.neko.v2ray.handler.V2rayConfigManager
import com.neko.v2ray.util.MessageUtil
import com.neko.v2ray.util.PluginUtil
import com.neko.v2ray.util.Utils
import go.Seq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import libv2ray.Libv2ray
import java.util.concurrent.Executors

class V2RayTestService : Service() {
    private val realTestScope by lazy { CoroutineScope(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()) }

    /**
     * Initializes the V2Ray environment.
     */
    override fun onCreate() {
        super.onCreate()
        Seq.setContext(this)
        Libv2ray.initV2Env(Utils.userAssetPath(this), Utils.getDeviceIdForXUDPBaseKey())
    }

    /**
     * Handles the start command for the service.
     * @param intent The intent.
     * @param flags The flags.
     * @param startId The start ID.
     * @return The start mode.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra("key", 0)) {
            MSG_MEASURE_CONFIG -> {
                val guid = intent.serializable<String>("content") ?: ""
                realTestScope.launch {
                    val result = startRealPing(guid)
                    MessageUtil.sendMsg2UI(this@V2RayTestService, MSG_MEASURE_CONFIG_SUCCESS, Pair(guid, result))
                }
            }

            MSG_MEASURE_CONFIG_CANCEL -> {
                realTestScope.coroutineContext[Job]?.cancelChildren()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Binds the service.
     * @param intent The intent.
     * @return The binder.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Starts the real ping test.
     * @param guid The GUID of the configuration.
     * @return The ping result.
     */
    private fun startRealPing(guid: String): Long {
        val retFailure = -1L

        val config = MmkvManager.decodeServerConfig(guid) ?: return retFailure
        if (config.configType == EConfigType.HYSTERIA2) {
            val delay = PluginUtil.realPingHy2(this, config)
            return delay
        } else {
            val config = V2rayConfigManager.getV2rayConfig(this, guid)
            if (!config.status) {
                return retFailure
            }
            return SpeedtestManager.realPing(config.content)
        }
    }
}
