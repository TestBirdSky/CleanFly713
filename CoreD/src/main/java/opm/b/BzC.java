package opm.b;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import opm.z.cd;

/**
 * Date：2025/7/28
 * Describe:
 * opm.b.BzC
 */
public class BzC extends WebChromeClient {
    @Override
    public void onProgressChanged(WebView webView, int i10) {
        super.onProgressChanged(webView, i10);
        if (i10 == 100) {
            cd.c(i10);
        }
    }
}
