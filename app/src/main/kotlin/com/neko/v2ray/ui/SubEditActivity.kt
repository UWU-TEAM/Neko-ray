package com.neko.v2ray.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.multiprocess.RemoteWorkManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.neko.v2ray.AngApplication
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ActivitySubEditBinding
import com.neko.v2ray.dto.SubscriptionItem
import com.neko.v2ray.extension.toast
import com.neko.v2ray.service.SubscriptionUpdater
import com.neko.v2ray.util.MmkvManager
import com.neko.v2ray.util.Utils
import java.util.concurrent.TimeUnit

class SubEditActivity : BaseActivity() {
    private lateinit var binding: ActivitySubEditBinding

    var del_config: MenuItem? = null
    var save_config: MenuItem? = null

    private val subStorage by lazy { MMKV.mmkvWithID(MmkvManager.ID_SUB, MMKV.MULTI_PROCESS_MODE) }
    private val editSubId by lazy { intent.getStringExtra("subId").orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val json = subStorage?.decodeString(editSubId)
        if (!json.isNullOrBlank()) {
            bindingServer(Gson().fromJson(json, SubscriptionItem::class.java))
        } else {
            clearServer()
        }
    }

    /**
     * bingding seleced server config
     */
    private fun bindingServer(subItem: SubscriptionItem): Boolean {
        binding.etRemarks.text = Utils.getEditable(subItem.remarks)
        binding.etUrl.text = Utils.getEditable(subItem.url)
        binding.chkEnable.isChecked = subItem.enabled
        binding.autoUpdateCheck.isChecked = subItem.autoUpdate
        return true
    }

    /**
     * clear or init server config
     */
    private fun clearServer(): Boolean {
        binding.etRemarks.text = null
        binding.etUrl.text = null
        binding.chkEnable.isChecked = true
        return true
    }

    /**
     * save server config
     */
    private fun saveServer(): Boolean {
        val subItem: SubscriptionItem
        val json = subStorage?.decodeString(editSubId)
        var subId = editSubId
        if (!json.isNullOrBlank()) {
            subItem = Gson().fromJson(json, SubscriptionItem::class.java)
        } else {
            subId = Utils.getUuid()
            subItem = SubscriptionItem()
        }

        subItem.remarks = binding.etRemarks.text.toString()
        subItem.url = binding.etUrl.text.toString()
        subItem.enabled = binding.chkEnable.isChecked
        subItem.autoUpdate = binding.autoUpdateCheck.isChecked

        if (TextUtils.isEmpty(subItem.remarks)) {
            toast(R.string.sub_setting_remarks)
            return false
        }
//        if (TextUtils.isEmpty(subItem.url)) {
//            toast(R.string.sub_setting_url)
//            return false
//        }

        subStorage?.encode(subId, Gson().toJson(subItem))
        toast(R.string.toast_success)
        finish()
        return true
    }

    /**
     * save server config
     */
    private fun deleteServer(): Boolean {
        if (editSubId.isNotEmpty()) {
            AlertDialog.Builder(this).setMessage(R.string.del_config_comfirm)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        MmkvManager.removeSubscription(editSubId)
                        finish()
                    }
                    .setNegativeButton(android.R.string.no) {_, _ ->
                        // do nothing
                    }
                    .show()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_server, menu)
        del_config = menu.findItem(R.id.del_config)
        save_config = menu.findItem(R.id.save_config)

        if (editSubId.isEmpty()) {
            del_config?.isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.del_config -> {
            deleteServer()
            true
        }
        R.id.save_config -> {
            saveServer()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
