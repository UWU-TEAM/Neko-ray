package com.neko.v2ray.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.neko.v2ray.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.neko.v2ray.databinding.ActivitySubSettingBinding
import com.neko.v2ray.databinding.LayoutProgressBinding
import com.neko.v2ray.dto.SubscriptionItem
import com.neko.v2ray.extension.toast
import com.neko.v2ray.util.AngConfigManager
import com.neko.v2ray.util.MmkvManager
import com.neko.v2ray.util.SoftInputAssist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SubSettingActivity : BaseActivity() {
    private lateinit var binding: ActivitySubSettingBinding
    private lateinit var softInputAssist: SoftInputAssist

    var subscriptions: List<Pair<String, SubscriptionItem>> = listOf()
    private val adapter by lazy { SubSettingRecyclerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        softInputAssist = SoftInputAssist(this)
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
        subscriptions = MmkvManager.decodeSubscriptions()
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_sub_setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_config -> {
            startActivity(Intent(this, SubEditActivity::class.java))
            true
        }

        R.id.sub_update -> {
            val dialog = MaterialAlertDialogBuilder(this)
                .setView(LayoutProgressBinding.inflate(layoutInflater).root)
                .setCancelable(false)
                .show()

            lifecycleScope.launch(Dispatchers.IO) {
                val count = AngConfigManager.updateConfigViaSubAll()
                delay(500L)
                launch(Dispatchers.Main) {
                    if (count > 0) {
                        toast(R.string.toast_success)
                    } else {
                        toast(R.string.toast_failure)
                    }
                    dialog.dismiss()
                }
            }

            true
        }

        else -> super.onOptionsItemSelected(item)

    }
}
