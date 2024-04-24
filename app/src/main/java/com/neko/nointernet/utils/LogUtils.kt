package com.neko.nointernet.utils

import android.util.Log
import com.neko.v2ray.BuildConfig

internal object LogUtils {
    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }
}