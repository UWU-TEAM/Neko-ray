package com.v2ray.ang.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.neko.tools.NetworkSwitcher
import com.v2ray.ang.R

class ToolsActivity : UwuCollapsingToolbarActivity() {
    override fun getFragment(): Fragment {
        return AsFragment()
    }

    fun uwuNetworkSwitcher(view: View) {
        startActivity(Intent(this, NetworkSwitcher::class.java))
    }

    class AsFragment : ResourceSettingsFragment() {
        init {
            preferencesResource = R.xml.uwu_preferences_tools
        }
    }
}
