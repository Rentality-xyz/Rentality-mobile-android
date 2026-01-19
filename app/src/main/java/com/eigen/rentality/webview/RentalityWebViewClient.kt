package com.eigen.rentality.webview

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class RentalityWebViewClient(
    private val onPageFinished: () -> Unit,
    private val onError: () -> Unit,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onPageFinished()
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)
        if (request.isForMainFrame) {
            onError()
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url?.toString() ?: return false
        Log.d("WebView", "shouldOverrideUrlLoading: $url")

        // 1️⃣ Wallet / deep links — В ЗОВНІШНІЙ APP
        if (
            url.startsWith("intent:") ||
            url.startsWith("market://") ||
            url.startsWith("metamask://") ||
            url.startsWith("wc:") ||
            url.startsWith("https://metamask.app.link")
        ) {
            try {
                val intent = if (url.startsWith("intent:")) {
                    Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                } else {
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                view?.context?.startActivity(intent)
            } catch (e: Exception) {
                Log.e("WebView", "Failed to open external intent", e)
            }
            return true
        }

        // 2️⃣ Звичайні http(s) — залишаємо у WebView
        return false
    }
}