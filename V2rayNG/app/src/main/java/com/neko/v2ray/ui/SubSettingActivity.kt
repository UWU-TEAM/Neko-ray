package com.neko.v2ray.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ActivitySubSettingBinding
import com.neko.v2ray.dto.SubscriptionItem
import com.neko.v2ray.extension.toast
import com.neko.v2ray.handler.AngConfigManager
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.helper.SimpleItemTouchHelperCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.neko.v2ray.util.SoftInputAssist

class SubSettingActivity : BaseActivity() {
    private val binding by lazy { ActivitySubSettingBinding.inflate(layoutInflater) }
    private lateinit var softInputAssist: SoftInputAssist

    var subscriptions: List<Pair<String, SubscriptionItem>> = listOf()
    private val adapter by lazy { SubSettingRecyclerAdapter(this) }
    private var mItemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        softInputAssist = SoftInputAssist(this)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        addCustomDividerToRecyclerView(binding.recyclerView, this, R.drawable.custom_divider)
        binding.recyclerView.adapter = adapter

        mItemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(adapter))
        mItemTouchHelper?.attachToRecyclerView(binding.recyclerView)
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
        refreshData()
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
            binding.pbWaiting.show()

            lifecycleScope.launch(Dispatchers.IO) {
                val count = AngConfigManager.updateConfigViaSubAll()
                delay(500L)
                launch(Dispatchers.Main) {
                    if (count > 0) {
                        toast(R.string.toast_success)
                    } else {
                        toast(R.string.toast_failure)
                    }
                    binding.pbWaiting.hide()
                }
            }

            true
        }

        else -> super.onOptionsItemSelected(item)

    }

    fun refreshData() {
        subscriptions = MmkvManager.decodeSubscriptions()
        adapter.notifyDataSetChanged()
    }
}
