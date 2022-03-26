package com.gary.other.keyBoard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gary.sdk.selfCheck.R;

public class testA extends Activity {

    private static WebView webviewHTML;
    private static View viewRootHTML;
    private static int iViewRootHTMLHeightDifferent;

    public static Context contextBrowser;{
        contextBrowser = this;
    }

    public class webViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (view == webviewHTML) super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (view == webviewHTML) super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (view == webviewHTML) view.loadUrl(url);
            return false;
// return super.shouldOverrideUrlLoading( view, url);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        super.dispatchTouchEvent(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) return true;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
        }
        return false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean eFocus) {
        super.onWindowFocusChanged(eFocus);
        if (eFocus == false) {
            fKeyboardClose();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    } catch (Exception e) {
                    }
                }
            }).start();
        }
    }

    private void fKeyboardClose() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            viewRootHTML.getWindowVisibleDisplayFrame(rect);
            iViewRootHTMLHeightDifferent = viewRootHTML.getRootView().getHeight() - (rect.bottom - rect.top);
            if (iViewRootHTMLHeightDifferent > 50) fKeyboardClose();
        }
    };

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            viewRootHTML = findViewById(R.id.bt_start);
            viewRootHTML.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
            WebSettings webSettings = webviewHTML.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webviewHTML.setWebViewClient(new WebViewClient());
            webviewHTML.loadUrl("");
        }
    }
}