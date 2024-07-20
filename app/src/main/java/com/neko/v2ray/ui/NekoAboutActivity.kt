package com.neko.v2ray.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mikepenz.aboutlibraries.LibsBuilder
import com.neko.appupdater.AppUpdater
import com.neko.appupdater.enums.Display
import com.neko.appupdater.enums.UpdateFrom
import com.neko.changelog.Changelog
import com.neko.v2ray.AppConfig
import com.neko.v2ray.util.Utils
import com.neko.v2ray.extension.toast
import com.neko.v2ray.R

class NekoAboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uwu_about_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.content_wrapper, NekoAboutFragment()).commit()
    }

    fun uwuUpdater(view: View) {
        val appUpdater = AppUpdater(this)
        appUpdater.setUpdateFrom(UpdateFrom.JSON)
        appUpdater.setUpdateJSON(AppConfig.UWU_UPDAYE_URL)
        appUpdater.setDisplay(Display.DIALOG)
        appUpdater.showAppUpdated(true)
        appUpdater.setCancelable(false)
        appUpdater.setButtonDoNotShowAgain("")
        appUpdater.start()
    }

    fun uwuRepository(view: View) {
        Utils.openUri(this, AppConfig.v2rayNGUrl)
    }

    fun promotion(view: View) {
        Utils.openUri(this, AppConfig.PromotionUrl)
    }

    fun license(view: View) {
        LibsBuilder()
            .withSortEnabled(true)
            .withLicenseShown(true)
            .withLicenseDialog(true)
            .withVersionShown(true)
            .withAboutIconShown(true)
            .withAboutMinimalDesign(false)
            .withAboutVersionShown(true)
            .withAboutVersionShownName(true)
            .withAboutVersionShownCode(true)
            .withSearchEnabled(true)
            .withActivityTitle(getString(R.string.uwu_license_title))
            .start(this)
    }

    fun privacypolicy(view: View) {
        Utils.openUri(this, AppConfig.v2rayNGPrivacyPolicy)
    }

    fun uwumodder(view: View) {
        Utils.openUri(this, AppConfig.TgChannelUrl)
    }

    fun uwuCredits(view: View) {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    fun changelog(view: View) {
        Changelog.createDialog(this).show()
    }
}
