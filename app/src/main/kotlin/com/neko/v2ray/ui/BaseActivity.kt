package com.neko.v2ray.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.neko.v2ray.util.MyContextWrapper
import com.neko.v2ray.util.Utils
import com.neko.themeengine.ThemeEngine

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeEngine.applyToActivity(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // Handles the home button press by delegating to the onBackPressedDispatcher.
            // This ensures consistent back navigation behavior.
            onBackPressedDispatcher.onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun attachBaseContext(newBase: Context?) {
        val context = newBase?.let {
            MyContextWrapper.wrap(newBase,  Utils.getLocale())
        }
        super.attachBaseContext(context)
    }
}
