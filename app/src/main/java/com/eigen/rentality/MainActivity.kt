package com.eigen.rentality

import android.Manifest
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
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eigen.rentality.webview.RentalityWebChromeClient
import com.eigen.rentality.webview.RentalityWebView
import com.eigen.rentality.webview.RentalityWebViewClient
import java.io.File

const val RENTALITY_URL = "https://app.rentality.xyz/"

class MainActivity : AppCompatActivity() {

    private val onCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = RentalityWebChromeClient.uri
            val filePathCallback = RentalityWebChromeClient.filePathCallback
            if (it.resultCode == -1) {
                if (it.data?.data == null) {
                    if (uri != null) {
                        filePathCallback?.onReceiveValue(arrayOf(uri))
                    } else {
                        filePathCallback?.onReceiveValue(null)
                    }
                } else {
                    filePathCallback?.onReceiveValue(arrayOf(it.data!!.data!!))
                }
            } else {
                filePathCallback?.onReceiveValue(null)
            }
        }

    private val onPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (!it) {
            RentalityWebChromeClient.filePathCallback?.onReceiveValue(null)
        } else {
            //files
            val getContent = Intent(Intent.ACTION_GET_CONTENT)
            getContent.type = "*/*"
            getContent.addCategory(Intent.CATEGORY_OPENABLE)

            //camera
            val fileTemp = File.createTempFile(
                "img",
                ".jpg",
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            RentalityWebChromeClient.uri = FileProvider.getUriForFile(
                this,
                this.application.packageName, fileTemp
            )
            val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, RentalityWebChromeClient.uri)

            val putter = Intent(Intent.ACTION_CHOOSER)
            putter.putExtra(Intent.EXTRA_INTENT, getContent)
            putter.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(camIntent))
            onCameraResult.launch(putter)
        }
    }

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
        webView.webChromeClient = RentalityWebChromeClient(
            onRequestPermission = {
                onPermissionResult.launch(Manifest.permission.CAMERA)
            }
        )

        onBackPressedDispatcher.addCallback(this) {
            webView.goBack()
        }
        webView.loadUrl(RENTALITY_URL)

    }

}
