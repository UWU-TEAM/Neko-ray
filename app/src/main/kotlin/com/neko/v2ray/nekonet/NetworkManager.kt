package com.neko.v2ray.nekonet

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.NetworkCapabilities
import android.net.TrafficStats

class NetworkManager(
    context: Context, private val subscriberId: String?
) {
    private var statsManager: NetworkStatsManager? = null
    private var lastTXByte = 0L
    private var lastRXByte = 0L
    private var lastMobileTXByte = 0L
    private var lastMobileRXByte = 0L
    private var lastTime = 0L


    init {
        statsManager =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        lastTXByte = TrafficStats.getTotalTxBytes()
        lastRXByte = TrafficStats.getTotalRxBytes()
        lastMobileTXByte = TrafficStats.getMobileTxBytes()
        lastMobileRXByte = TrafficStats.getMobileRxBytes()
        lastTime = System.currentTimeMillis()
    }


    fun getUsageNow(networkType: NetworkType): Usage {

        val currentRXByte = TrafficStats.getTotalRxBytes()
        val currentTXByte = TrafficStats.getTotalTxBytes()
        val currentMobileRXByte = TrafficStats.getMobileRxBytes()
        val currentMobileTXByte = TrafficStats.getMobileTxBytes()
        val currentTime = System.currentTimeMillis()

        val usedRxByte = currentRXByte - lastRXByte
        val usedTxByte = currentTXByte - lastTXByte
        val usedMobileRxByte = currentMobileRXByte - lastMobileRXByte
        val usedMobileTxByte = currentMobileTXByte - lastMobileTXByte
        val usedTime = currentTime - lastTime

        //update last values
        lastRXByte = currentRXByte
        lastTXByte = currentTXByte
        lastMobileRXByte = currentMobileRXByte
        lastMobileTXByte = currentMobileTXByte
        lastTime = currentTime

        return when (networkType) {
            NetworkType.MOBILE -> {
                Usage(
                    usedMobileRxByte, usedMobileTxByte, usedTime
                )
            }
            NetworkType.WIFI -> {
                Usage(
                    usedRxByte - usedMobileRxByte, usedTxByte - usedMobileTxByte, usedTime
                )
            }
            NetworkType.ALL -> {
                Usage(
                    usedRxByte, usedTxByte, usedTime
                )
            }
        }
    }
}