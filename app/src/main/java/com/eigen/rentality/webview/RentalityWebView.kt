package com.eigen.rentality.webview

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast

@SuppressLint("SetJavaScriptEnabled")
class RentalityWebView(
    context: Context,
    attrs: AttributeSet
) : WebView(context,attrs) {

    init {
        settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            databaseEnabled = true
            domStorageEnabled = true
            allowFileAccessFromFileURLs = true
            allowContentAccess = true
            useWideViewPort = true
            mixedContentMode = 0
            allowUniversalAccessFromFileURLs = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_DEFAULT
            offscreenPreRaster = true
            setSupportMultipleWindows(false)
            userAgentString = System.getProperty("http.agent")!!
        }
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(this@RentalityWebView, true)
        }
    }

}