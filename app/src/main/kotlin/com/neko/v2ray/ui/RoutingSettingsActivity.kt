package com.neko.v2ray.ui

import android.content.Intent
import android.os.Bundle
import com.neko.v2ray.R
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.neko.v2ray.AppConfig
import com.neko.v2ray.databinding.ActivityRoutingSettingsBinding
import com.neko.v2ray.util.SoftInputAssist

class RoutingSettingsActivity : BaseActivity() {
    private lateinit var binding: ActivityRoutingSettingsBinding
    private lateinit var softInputAssist: SoftInputAssist

    private val titles: Array<out String> by lazy {
        resources.getStringArray(R.array.routing_tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutingSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(RoutingSettingsFragment().newInstance(AppConfig.PREF_V2RAY_ROUTING_AGENT))
        fragments.add(RoutingSettingsFragment().newInstance(AppConfig.PREF_V2RAY_ROUTING_DIRECT))
        fragments.add(RoutingSettingsFragment().newInstance(AppConfig.PREF_V2RAY_ROUTING_BLOCKED))

        val adapter = FragmentAdapter(this, fragments)
        binding.viewpager.adapter = adapter
        //tablayout.setTabTextColors(Color.BLACK, Color.RED)
        TabLayoutMediator(binding.tablayout, binding.viewpager) { tab, position ->
            tab.text = titles[position]
        }.attach()
        softInputAssist = SoftInputAssist(this)
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()
    }
}
