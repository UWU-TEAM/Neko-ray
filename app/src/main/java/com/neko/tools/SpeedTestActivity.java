package com.neko.tools;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.v2ray.ang.ui.BaseActivity;
import com.v2ray.ang.R;

public class SpeedTestActivity extends BaseActivity {
    ProgressBar progressBar;
    TextView txtwait;
    WebView web;

    public class myWebClient extends WebViewClient {
        private final SpeedTestActivity this$0;

        public myWebClient(SpeedTestActivity speedTestActivity) {
            this.this$0 = speedTestActivity;
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            this.this$0.txtwait.setVisibility(View.GONE);
            this.this$0.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            webView.loadUrl(str);
            return true;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.uwu_speedtest);
        this.web = (WebView) findViewById(R.id.uwu_WebView);
        this.progressBar = (ProgressBar) findViewById(R.id.uwu_ProgressBar);
        this.txtwait = (TextView) findViewById(R.id.uwu_TextView);
        this.web.setWebViewClient(new myWebClient(this));
        this.web.getSettings().setJavaScriptEnabled(true);
        this.web.loadUrl("https://fusiontempest.speedtestcustom.com");
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4 && this.web.canGoBack()) {
            this.web.goBack();
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }
}
