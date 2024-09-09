package com.eigen.rentality

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

const val RENTALITY_URL = "https://app.rentality.xyz"

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            println("Permission is granted")
        } else {
            println("Permission is not granted")
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        val webView = findViewById<WebView>(R.id.webView)
        val loader = findViewById<View>(R.id.loader)

        webView.apply {
            val instance: CookieManager = CookieManager.getInstance()
            instance.setAcceptCookie(true)
            instance.setAcceptThirdPartyCookies(this, true)
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowContentAccess = true
            settings.mixedContentMode = 0
            settings.useWideViewPort = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.loadWithOverviewMode = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.offscreenPreRaster = true
            webView.addJavascriptInterface(object {
                @JavascriptInterface
                fun downloadFile(url: String, fileName: String) {
                    val request = DownloadManager.Request(Uri.parse(url))
                    request.setTitle(fileName)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, null, null))
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)

                    Toast.makeText(applicationContext, "Downloading $fileName", Toast.LENGTH_LONG).show()
                }
            }, "Android")

            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.loadUrl("""
                           javascript:(function() {
                                function replaceIframes() {
                                    var iframes = document.getElementsByTagName('iframe');
                                    var iframesArray = Array.from(iframes); 
                                    for (var i = 0; i < iframesArray.length; i++) {
                                        var iframe = iframesArray[i];
                                        var src = iframe.src;
                                        if (src.indexOf('.pdf') != -1) {
                                            var iframeWidth = iframe.width || iframe.style.width;
                                            var iframeHeight = '600px';
                                            var wrapper = document.createElement('div');
                                            wrapper.style.width = iframeWidth;
                                            wrapper.style.height = iframeHeight;
                                            wrapper.style.display = 'flex';
                                            wrapper.style.background = '#202125';
                                            wrapper.style.color = 'black';
                                            wrapper.style.flexDirection = 'column';
                                            wrapper.style.justifyContent = 'center';
                                            wrapper.style.alignItems = 'center';
                                            wrapper.style.border = iframe.style.border;  
                                            
                                            var button = document.createElement('button');
                                            button.innerHTML = 'Download PDF';
                                            button.style.padding = '10px 20px';
                                            button.style.fontSize = '16px';
                                            button.style.backgroundColor = '#89B4F8';
                                            button.style.color = 'black';
                                            button.style.border = 'none';
                                            button.style.borderRadius = '16px';
                                            button.style.cursor = 'pointer';
                                            button.style.marginBottom = '20px';
                                            button.style.marginTop = iframe.style.height;
                                            button.onclick = (function(src) {
                                                return function() {
                                                    window.Android.downloadFile(src, src.substring(src.lastIndexOf('/') + 1));
                                                };
                                            })(src);
                    
                                            wrapper.appendChild(button);
                                            iframe.parentNode.replaceChild(wrapper, iframe);
                                        }
                                    }
                                }
                    
                                replaceIframes();
                    
                                var observer = new MutationObserver(function(mutations) {
                                    mutations.forEach(function(mutation) {
                                        replaceIframes();
                                    });
                                });
                    
                                observer.observe(document.body, { childList: true, subtree: true });
                    
                    })();
                    """.trimIndent())
                    webView.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    println(url)

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

            loadUrl(RENTALITY_URL)
        }

        onBackPressedDispatcher.addCallback(this) {
            webView.goBack()
        }

    }

}