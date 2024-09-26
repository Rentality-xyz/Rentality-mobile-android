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
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowContentAccess = true
            settings.useWideViewPort = true
            settings.mixedContentMode = 0
            settings.allowUniversalAccessFromFileURLs = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.offscreenPreRaster = true
            settings.setSupportMultipleWindows(false)
        }
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(this@RentalityWebView, true)
        }
        addJavascriptInterface(object {
            @JavascriptInterface
            fun downloadFile(url: String, fileName: String) {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setTitle(fileName)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    URLUtil.guessFileName(url, null, null)
                )
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

                Toast.makeText(context, "Downloading $fileName", Toast.LENGTH_LONG)
                    .show()
            }
        }, "Android")
    }

}