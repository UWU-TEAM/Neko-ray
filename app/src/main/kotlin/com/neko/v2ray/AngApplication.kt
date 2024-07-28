package com.neko.v2ray

import android.content.Context
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.tencent.mmkv.MMKV

class AngApplication : MultiDexApplication(), Configuration.Provider {
    override val workManagerConfiguration: Configuration = Configuration.Builder().setDefaultProcessName("${BuildConfig.APPLICATION_ID}:bg").build()

    companion object {
        lateinit var application: AngApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}
