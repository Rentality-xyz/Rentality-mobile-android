package com.eigen.rentality.webview

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class RentalityWebChromeClient(
    private val onRequestPermission: () -> Unit
): WebChromeClient() {

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        RentalityWebChromeClient.filePathCallback = filePathCallback
        onRequestPermission()
        return true
    }


    companion object {
        var uri: Uri? = null
        var filePathCallback: ValueCallback<Array<Uri>>? = null
    }

}