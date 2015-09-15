package com.android.webview.nullwebview;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebIconDatabase;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebView.PrivateAccess;
import android.webkit.WebViewDatabase;
import android.webkit.WebViewFactoryProvider;
import android.webkit.WebViewFactoryProvider.Statics;
import android.webkit.WebViewProvider;

public class NullWebViewFactoryProvider implements WebViewFactoryProvider {
    public Statics getStatics() {
        throw new UnsupportedOperationException();
    }

    public WebViewProvider createWebView(WebView webView, PrivateAccess privateAccess) {
        throw new UnsupportedOperationException();
    }

    public GeolocationPermissions getGeolocationPermissions() {
        throw new UnsupportedOperationException();
    }

    public CookieManager getCookieManager() {
        throw new UnsupportedOperationException();
    }

    public WebIconDatabase getWebIconDatabase() {
        throw new UnsupportedOperationException();
    }

    public WebStorage getWebStorage() {
        throw new UnsupportedOperationException();
    }

    public WebViewDatabase getWebViewDatabase(Context context) {
        throw new UnsupportedOperationException();
    }
}
