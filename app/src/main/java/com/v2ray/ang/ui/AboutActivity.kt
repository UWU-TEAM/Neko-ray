package com.v2ray.ang.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.neko.appupdater.AppUpdater
import com.neko.appupdater.enums.Display
import com.neko.appupdater.enums.UpdateFrom
import com.neko.changelog.Changelog
import com.v2ray.ang.util.SpeedtestUtil
import com.v2ray.ang.extension.toast
import com.v2ray.ang.R

class AboutActivity : UwuCollapsingToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        isStoragePermissionGranted()
        super.onCreate(savedInstanceState)
    }

    override fun getFragment(): Fragment {
        return AsFragment()
    }

    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission("android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_MEDIA_IMAGES"), 1)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_EXTERNAL_STORAGE"), 1)
        }
        return false
    }

    companion object {
        fun repositoryUrl(context: Context) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Blawuken/Neko_v2rayNG")))
        }

        fun promotionUrl(context: Context) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://9.234456.xyz/abc.html?t=1703789826882")))
        }

        fun feedbackUrl(context: Context) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Blawuken/Neko_v2rayNG/issues")))
        }

        fun privacyUrl(context: Context) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://raw.githubusercontent.com/Blawuken/Neko_v2rayNG/main/CR.md")))
        }

        fun modderUrl(context: Context) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/uwuresourceguide")))
        }
    }

    fun uwuUpdater(view: View) {
        AppUpdater(this)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("Blawuken", "Neko_v2rayNG")
            .setDisplay(Display.DIALOG)
            .showAppUpdated(true)
            .start()
    }

    fun uwuRepository(view: View) {
        repositoryUrl(this)
    }

    fun promotion(view: View) {
        promotionUrl(this)
    }

    fun feedback(view: View) {
        feedbackUrl(this)
    }

    fun privacypolicy(view: View) {
        privacyUrl(this)
    }

    fun uwumodder(view: View) {
        modderUrl(this)
    }

    fun uwuCredits(view: View) {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    fun changelog(view: View) {
        Changelog.createDialog(this).show()
    }

    class AsFragment : ResourceSettingsFragment() {
        init {
            preferencesResource = R.xml.uwu_preferences_about
        }
    }
}
