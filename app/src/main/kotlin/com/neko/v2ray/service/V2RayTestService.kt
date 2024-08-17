package com.neko.v2ray.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.neko.v2ray.AppConfig.MSG_MEASURE_CONFIG
import com.neko.v2ray.AppConfig.MSG_MEASURE_CONFIG_CANCEL
import com.neko.v2ray.AppConfig.MSG_MEASURE_CONFIG_SUCCESS
import com.neko.v2ray.util.MessageUtil
import com.neko.v2ray.util.SpeedtestUtil
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
    private val realTestScope by lazy { CoroutineScope(Executors.newFixedThreadPool(10).asCoroutineDispatcher()) }

    override fun onCreate() {
        super.onCreate()
        Seq.setContext(this)
        Libv2ray.initV2Env(Utils.userAssetPath(this), Utils.getDeviceIdForXUDPBaseKey())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.getIntExtra("key", 0)) {
            MSG_MEASURE_CONFIG -> {
                val contentPair = intent.getSerializableExtra("content") as Pair<String, String>
                realTestScope.launch {
                    val result = SpeedtestUtil.realPing(contentPair.second)
                    MessageUtil.sendMsg2UI(this@V2RayTestService, MSG_MEASURE_CONFIG_SUCCESS, Pair(contentPair.first, result))
                }
            }

            MSG_MEASURE_CONFIG_CANCEL -> {
                realTestScope.coroutineContext[Job]?.cancelChildren()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
