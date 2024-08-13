package com.neko.v2ray.nekonet

import java.util.*
import kotlin.collections.List

data class Speed(
    val speed: String,
    val unit: String
)

object NetSpeed {

    private var speedValue = ""
    private var speedUnit = ""

    private var totalSpeed: Long = 0L
    private var downSpeed: Long = 0L
    private var upSpeed: Long = 0L
    private var isSpeedUnitBits: Boolean = false

    fun setSpeedUnitBits(isSpeedUnitBits: Boolean) {
        this.isSpeedUnitBits = isSpeedUnitBits
    }

    fun getSpeedUnitBits(): Boolean {
        return isSpeedUnitBits
    }

    private fun getSpeed(s: Long): Speed {
        var speed = s

        if (isSpeedUnitBits) {
            speed *= 8
        }

        if (speed < 1000000) {
            speedUnit =
                if (isSpeedUnitBits) "kb/s" else "kB/s"
            speedValue = (speed / 1000).toString()
        } else if (speed >= 1000000) {
            speedUnit =
                if (isSpeedUnitBits) "mb/s" else "MB/s"

            speedValue = if (speed < 10000000) {
                java.lang.String.format(Locale.ENGLISH, "%.1f", speed / 1000000.0)
            } else if (speed < 100000000) {
                (speed / 1000000).toString()
            } else {
                "99+"
            }
        } else {
            speedValue = "-"
            speedUnit = "-"
        }

        return Speed(speedValue, speedUnit)
    }

    fun calculateSpeed(timeTaken: Long, downBytes: Long, upBytes: Long): List<Speed> {
        var totalSpeed: Long = 0
        var downSpeed: Long = 0
        var upSpeed: Long = 0
        val totalBytes = downBytes + upBytes
        if (timeTaken > 0) {
            totalSpeed = totalBytes * 1000 / timeTaken
            downSpeed = downBytes * 1000 / timeTaken
            upSpeed = upBytes * 1000 / timeTaken
        }
        this.totalSpeed = totalSpeed
        this.downSpeed = downSpeed
        this.upSpeed = upSpeed

        return listOf(
            getSpeed(totalSpeed),
            getSpeed(downSpeed),
            getSpeed(upSpeed)
        )
    }

}