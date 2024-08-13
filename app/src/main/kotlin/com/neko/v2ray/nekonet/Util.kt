package com.neko.v2ray.nekonet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager


object Util {

    @SuppressLint("HardwareIds")
    fun getSubscriberId(context: Context): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var id = ""
            try {
                id = telephonyManager.subscriberId
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return id
        }
        return null
    }

    fun formatData(sent: Long, received: Long): List<String> {
        val total = sent + received
        val data: List<String>
        val totalBytes = total / 1024f
        val sentBytes = sent / 1024f
        val receivedBytes = received / 1024f
        val totalMB = totalBytes / 1024f
        val totalGB: Float
        val sentGB: Float
        val receivedGB: Float
        val sentMB: Float = sentBytes / 1024f
        val receivedMB: Float = receivedBytes / 1024f
        var sentData = ""
        var receivedData = ""
        val totalData: String
        if (totalMB > 1024) {
            totalGB = totalMB / 1024f
            totalData = String.format("%.2f", totalGB) + " GB"
        } else {
            totalData = String.format("%.2f", totalMB) + " MB"
        }
        if (sentMB > 1024) {
            sentGB = sentMB / 1024f
            sentData = String.format("%.2f", sentGB) + " GB"
        } else {
            sentData = String.format("%.2f", sentMB) + " MB"
        }
        if (receivedMB > 1024) {
            receivedGB = receivedMB / 1024f
            receivedData = String.format("%.2f", receivedGB) + " GB"
        } else {
            receivedData = String.format("%.2f", receivedMB) + " MB"
        }
        data = listOf(sentData, receivedData, totalData)
        return data
    }
}