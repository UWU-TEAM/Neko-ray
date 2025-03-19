package com.neko.v2ray.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.service.V2RayServiceManager

class BootReceiver : BroadcastReceiver() {
    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * It checks if the context is not null and the action is ACTION_BOOT_COMPLETED.
     * If the conditions are met, it starts the V2Ray service.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!MmkvManager.decodeStartOnBoot() || MmkvManager.getSelectServer().isNullOrEmpty()) return
        V2RayServiceManager.startVService(context)
    }
}
