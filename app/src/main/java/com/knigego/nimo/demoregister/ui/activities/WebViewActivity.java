package com.knigego.nimo.demoregister.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.knigego.nimo.demoregister.AppConstants;
import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity {

    @Bind(R.id.webView)
    WebView mWebView;

    private String mTitle;

    @Override
    protected void _onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(false);

        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(new MyWebChromeClient());

        getIntentValue();
    }

    private void getIntentValue() {
        Intent intent =getIntent();
        mTitle = intent.getStringExtra(AppConstants.TITLE);
        String webUrl = intent.getStringExtra(AppConstants.WEB_URL);
        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }

        if (!TextUtils.isEmpty(webUrl)) {
            mWebView.loadUrl(webUrl);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
            }
        }
    }
}
