package com.caz;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Date：2025/7/28
 * Describe:
 * com.caz.Naz
 */
public class Naz extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);
        if (i10 == 100) {
            Ac.c(i10);
        }
    }
}
