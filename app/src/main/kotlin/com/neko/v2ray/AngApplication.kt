package com.neko.v2ray

import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.tencent.mmkv.MMKV
import com.neko.v2ray.util.SettingsManager

class AngApplication : MultiDexApplication() {
    companion object {
        lateinit var application: AngApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    private val workManagerConfiguration: Configuration = Configuration.Builder()
        .setDefaultProcessName("${BuildConfig.APPLICATION_ID}:bg")
        .build()

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        SettingsManager.initRoutingRulesets(this)
    }
}
