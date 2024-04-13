package com.neko.v2ray.ui

import android.os.Bundle
import com.neko.v2ray.AppConfig
import com.neko.v2ray.BuildConfig
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ActivityAboutBinding
import com.neko.v2ray.util.SpeedtestUtil
import com.neko.v2ray.util.Utils

class AboutActivity : BaseActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        title = getString(R.string.title_about)

        binding.layoutSoureCcode.setOnClickListener {
            Utils.openUri(this, AppConfig.v2rayNGUrl)
        }

        binding.layoutFeedback.setOnClickListener {
            Utils.openUri(this, AppConfig.v2rayNGIssues)
        }

        binding.layoutTgChannel.setOnClickListener {
            Utils.openUri(this, AppConfig.TgChannelUrl)
        }

        binding.layoutPrivacyPolicy.setOnClickListener {
            Utils.openUri(this, AppConfig.v2rayNGPrivacyPolicy)
        }

        "v${BuildConfig.VERSION_NAME} (${SpeedtestUtil.getLibVersion()})".also {
            binding.tvVersion.text = it
        }


    }


}
