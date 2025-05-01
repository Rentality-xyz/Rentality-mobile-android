package com.eigen.rentality.webview

import android.content.Intent
import android.net.Uri
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
        val url = request?.url.toString()
        println("shouldOverrideUrlLoading: $url")

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false
        } else {
            try {
                val intent: Intent
                if (url.startsWith("intent:")) {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                } else {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }
                view!!.context.startActivity(intent)

            } catch (_: Exception) {
            }
            return true
        }
    }
}