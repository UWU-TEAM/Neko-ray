package com.neko.v2ray.ui

import com.neko.v2ray.R
import com.neko.v2ray.util.Utils
import android.os.Bundle
import com.neko.v2ray.service.V2RayServiceManager

class ScSwitchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moveTaskToBack(true)

        setContentView(R.layout.activity_none)

        if (V2RayServiceManager.v2rayPoint.isRunning) {
            Utils.stopVService(this)
        } else {
            Utils.startVServiceFromToggle(this)
        }
        finish()
    }
}
