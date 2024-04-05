package com.neko.v2ray.service

import android.app.Service

interface ServiceControl {
    fun getService(): Service

    fun startService()

    fun stopService()

    fun vpnProtect(socket: Int): Boolean
}
