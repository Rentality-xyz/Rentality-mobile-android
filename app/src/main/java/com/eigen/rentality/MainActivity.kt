package com.eigen.rentality

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eigen.rentality.webview.RentalityWebView
import com.eigen.rentality.webview.RentalityWebViewClient

const val RENTALITY_URL = "https://app.rentality.xyz/"

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<RentalityWebView>(R.id.webView)
        val webViewContainer = findViewById<View>(R.id.webViewContainer)
        val loader = findViewById<View>(R.id.loader)
        val swipeRefreshContainer = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)

        swipeRefreshContainer.setOnRefreshListener {
            webView.reload()
            swipeRefreshContainer.isRefreshing = false
        }

        webView.webViewClient = RentalityWebViewClient(
            onPageFinished = {
                webViewContainer.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }
        )

        onBackPressedDispatcher.addCallback(this) {
            webView.goBack()
        }
        webView.loadUrl(RENTALITY_URL)

    }

}
