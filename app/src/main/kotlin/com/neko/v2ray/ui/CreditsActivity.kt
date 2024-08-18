package com.neko.v2ray.ui

import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.neko.v2ray.R

class CreditsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uwu_credits_activity)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.content_wrapper, CreditsFragment()).commit()
    }
}
