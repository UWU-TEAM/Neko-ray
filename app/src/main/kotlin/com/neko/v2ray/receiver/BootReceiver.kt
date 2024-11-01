package com.neko.v2ray.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.neko.v2ray.service.V2RayServiceManager
import com.neko.v2ray.handler.MmkvManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action && MmkvManager.decodeStartOnBoot()) {
            if (MmkvManager.getSelectServer().isNullOrEmpty()) {
                return
            }
            V2RayServiceManager.startV2Ray(context!!)
        }
    }
}