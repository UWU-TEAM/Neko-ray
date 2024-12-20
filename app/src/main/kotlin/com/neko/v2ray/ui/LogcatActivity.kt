package com.neko.v2ray.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.neko.v2ray.AppConfig.ANG_PACKAGE
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ActivityLogcatBinding
import com.neko.v2ray.extension.toast
import com.neko.v2ray.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LogcatActivity : BaseActivity() {
    private val binding by lazy {
        ActivityLogcatBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        logcat(false)
    }

    private fun logcat(shouldFlushLog: Boolean) {
        binding.pbWaiting.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Default) {
            try {
                if (shouldFlushLog) {
                    val lst = linkedSetOf("logcat", "-c")
                    withContext(Dispatchers.IO) {
                        val process = Runtime.getRuntime().exec(lst.toTypedArray())
                        process.waitFor()
                    }
                }
                val lst = linkedSetOf(
                    "logcat", "-d", "-v", "time", "-s",
                    "GoLog,tun2socks,$ANG_PACKAGE,AndroidRuntime,System.err"
                )
                val process = withContext(Dispatchers.IO) {
                    Runtime.getRuntime().exec(lst.toTypedArray())
                }
                val allText = process.inputStream.bufferedReader().use { it.readText() }
                withContext(Dispatchers.Main) {
                    binding.tvLogcat.text = allText
                    binding.tvLogcat.movementMethod = ScrollingMovementMethod()
                    binding.pbWaiting.visibility = View.GONE
                    Handler(Looper.getMainLooper()).post { binding.svLogcat.fullScroll(View.FOCUS_DOWN) }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    binding.pbWaiting.visibility = View.GONE
                    toast(R.string.toast_failure)
                }
                e.printStackTrace()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logcat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.copy_all -> {
            Utils.setClipboard(this, binding.tvLogcat.text.toString())
            toast(R.string.toast_success)
            true
        }

        R.id.clear_all -> {
            logcat(true)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
