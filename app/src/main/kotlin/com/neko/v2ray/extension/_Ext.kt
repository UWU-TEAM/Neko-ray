package com.neko.v2ray.extension

import android.content.Context
import android.os.Build
import android.widget.Toast
import com.neko.v2ray.AngApplication
import me.drakeet.support.toast.ToastCompat
import org.json.JSONObject
import java.net.URI
import java.net.URLConnection

val Context.v2RayApplication: AngApplication?
    get() = applicationContext as? AngApplication

fun Context.toast(message: Int) {
    ToastCompat.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: CharSequence) {
    ToastCompat.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun JSONObject.putOpt(pair: Pair<String, Any?>) {
    put(pair.first, pair.second)
}

fun JSONObject.putOpt(pairs: Map<String, Any?>) {
    pairs.forEach { put(it.key, it.value) }
}

const val THRESHOLD = 1000L
const val DIVISOR = 1024.0

fun Long.toSpeedString(): String = this.toTrafficString() + "/s"

fun Long.toTrafficString(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB")
    var size = this.toDouble()
    var unitIndex = 0
    while (size >= THRESHOLD && unitIndex < units.size - 1) {
        size /= DIVISOR
        unitIndex++
    }
    return String.format("%.1f %s", size, units[unitIndex])
}

val URLConnection.responseLength: Long
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        contentLengthLong
    } else {
        contentLength.toLong()
    }

val URI.idnHost: String
    get() = host?.replace("[", "")?.replace("]", "").orEmpty()

fun String.removeWhiteSpace(): String = replace("\\s+".toRegex(), "")

fun String.toLongEx(): Long = toLongOrNull() ?: 0