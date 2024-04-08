package com.neko.v2ray.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.neko.appupdater.AppUpdater
import com.neko.appupdater.enums.Display
import com.neko.appupdater.enums.UpdateFrom
import com.neko.changelog.Changelog
import com.neko.v2ray.util.SpeedtestUtil
import com.neko.v2ray.extension.toast
import com.neko.v2ray.R

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uwu_collapsing_toolbar)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarLayout: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.content_wrapper, AboutFragment()).commit()
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
}
