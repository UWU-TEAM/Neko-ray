package com.neko.tools

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView

import com.neko.v2ray.ui.BaseActivity
import com.neko.v2ray.R

class SpeedTestActivity : BaseActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var txtwait: TextView
    private lateinit var web: WebView

    private inner class MyWebClient : WebViewClient() {
        override fun onPageFinished(webView: WebView, url: String) {
            super.onPageFinished(webView, url)
            txtwait.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        override fun onPageStarted(webView: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(webView, url, favicon)
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uwu_speedtest)
        
        web = findViewById(R.id.uwu_WebView)
        progressBar = findViewById(R.id.uwu_ProgressBar)
        txtwait = findViewById(R.id.uwu_TextView)
        
        web.webViewClient = MyWebClient()
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        web.setInitialScale(1)
        web.clearCache(true)
        web.clearHistory()
        web.loadUrl("https://fusiontempest.speedtestcustom.com")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            web.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
