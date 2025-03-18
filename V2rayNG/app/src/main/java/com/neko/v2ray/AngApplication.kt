package com.neko.v2ray

import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkManager
import com.tencent.mmkv.MMKV
import com.neko.v2ray.AppConfig.ANG_PACKAGE
import com.neko.v2ray.handler.SettingsManager
import com.neko.v2ray.util.Utils

class AngApplication : MultiDexApplication() {
    companion object {
        lateinit var application: AngApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    private val workManagerConfiguration: Configuration = Configuration.Builder()
        .setDefaultProcessName("${ANG_PACKAGE}:bg")
        .build()

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        WorkManager.initialize(this, workManagerConfiguration)
        SettingsManager.initRoutingRulesets(this)
    }

}
