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

    fun getUsage(interval: TimeInterval, networkType: NetworkType): Usage {
        val stats = statsManager?.queryDetails(
            when (networkType) {
                NetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                NetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                NetworkType.ALL -> NetworkCapabilities.TRANSPORT_WIFI and NetworkCapabilities.TRANSPORT_CELLULAR
            }, subscriberId, interval.start, interval.end
        )

        val bucket = NetworkStats.Bucket()
        val usage = Usage()
        usage.timeTaken = interval.end - interval.start
        if (stats != null) {
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)

                usage.downloads += bucket.rxBytes
                usage.uploads += bucket.txBytes
            }
        }

        stats?.close()
        return usage
    }

    fun getMultiUsage(intervals: List<TimeInterval>, networkType: NetworkType): List<DataUsage> {
        var start = intervals[0].start
        var end = intervals[0].end

        val usages = mutableMapOf<TimeInterval, DataUsage>()

        for (interval in intervals) {
            if (interval.start < start) start = interval.start

            if (interval.end > end) end = interval.end

            usages[interval] = DataUsage(date = interval.date)
        }

        val stats = statsManager?.queryDetails(
            when (networkType) {
                NetworkType.MOBILE -> NetworkCapabilities.TRANSPORT_CELLULAR
                NetworkType.WIFI -> NetworkCapabilities.TRANSPORT_WIFI
                NetworkType.ALL -> NetworkCapabilities.TRANSPORT_WIFI and NetworkCapabilities.TRANSPORT_CELLULAR
            }, subscriberId, start, end
        )

        val bucket = NetworkStats.Bucket()

        if (stats != null) {
            while (stats.hasNextBucket()) {
                stats.getNextBucket(bucket)

                for (interval in intervals) {
                    if (checkBucketInterval(bucket, interval.start, interval.end)) {
                        usages[interval]!!.downloads += bucket.rxBytes
                        usages[interval]!!.uploads += bucket.txBytes
                    }
                }
            }
        }

        stats?.close()
        return usages.values.toList()
    }

    private fun checkBucketInterval(bucket: NetworkStats.Bucket, start: Long, end: Long): Boolean {
        return ((bucket.startTimeStamp in start..end) || (bucket.endTimeStamp in (start + 1) until end))
    }

}