package com.eigen.rentality

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.eigen.rentality.webview.RentalityView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentalityView()
        }
    }
}

//const val RENTALITY_URL = "https://app.rentality.io/"
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        val webView = findViewById<RentalityWebView>(R.id.webView)
////        val webViewContainer = findViewById<View>(R.id.webViewContainer)
////        val loader = findViewById<View>(R.id.loader)
////        val swipeRefreshContainer = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
////
////        swipeRefreshContainer.setOnRefreshListener {
////            webView.reload()
////            swipeRefreshContainer.isRefreshing = false
////        }
////
////        webView.webViewClient = RentalityWebViewClient(
////            onPageFinished = {
////                webViewContainer.visibility = View.VISIBLE
////                loader.visibility = View.GONE
////            },
////            onError = {
////
////            }
////        )
//
//
//    }
//
//}
