package com.firefly.walkietalkie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;

public class LiveWebView extends WebView {
    Context mContext;

    public LiveWebView(Context context, String URL) {
        super(context);
        mContext = context;
        setWebViewClient(URL);
    }

    public void executeJavascript(String function, String param) {
        this.loadUrl("javascript:" + function + "(\'" + param + "\')");
    }

    public void executeJavascript(String function, Object... params) {
        String strParam = params[0].toString();
        for (int i = 1; i < params.length; i++) {
            strParam = strParam + "," + params[i];
        }
        AppUtil.Log_WalkieTalkie("Request : " + function + "(" + strParam + ") to server");
        this.loadUrl("javascript:" + function + "(" + strParam + ")");
    }

    public void executeJavascript(String function, Boolean param) {
        this.loadUrl("javascript:" + function + "(" + param + ")");
    }

    public void executeJavascript(String function) {
        this.loadUrl("javascript:" + function + "()");
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    boolean setWebViewClient(String URL) {
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus(View.FOCUS_DOWN);

        WebSettings webSettings = getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        addJavascriptInterface(new WebAppInterface(mContext, this), "Android");

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        this.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadUrl(url);
                return true;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(mContext, "Error: " + description, Toast.LENGTH_SHORT).show();
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            public void onPageFinished(WebView view, String url) {
                mSharedPreferences.saveSession(mContext, CookieManager.getInstance().getCookie(url));
            }
        });
        this.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                // Always grant permission since the app itself requires location
                // permission and the user has therefore already granted it
                AppUtil.Log_WalkieTalkie("Origin : " + origin);
                callback.invoke(origin, true, false);
            }

            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return true;
            }

            public boolean onConsoleMessage(ConsoleMessage cm) {

                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                AppUtil.Log_WalkieTalkie("Show console messages, Used for debugging: " + message);

            }
        });
        loadUrl(URL);
        return true;
    }
}