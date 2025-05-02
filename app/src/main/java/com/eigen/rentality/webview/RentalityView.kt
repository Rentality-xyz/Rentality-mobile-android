package com.eigen.rentality.webview

import android.Manifest
import android.content.Loader
import android.os.Build.VERSION.SDK_INT
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.eigen.rentality.R

const val RENTALITY_URL = "https://app.rentality.io/"

@Composable
fun RentalityView() {

    val context = LocalContext.current

    val onCameraResult = rememberCameraLauncher()
    val onPermissionResult = rememberPermissionLauncher(context) { onCameraResult.launch(it) }

    val webViewState = remember { mutableStateOf(WebViewState.LOADING) }

    val webView = remember {
        RentalityWebView(context).apply {
            webViewClient = RentalityWebViewClient(
                onError = {
                    webViewState.value = WebViewState.ERROR
                },
                onPageFinished = {
                    if (webViewState.value == WebViewState.LOADING) {
                        webViewState.value = WebViewState.LOADED
                    }
                }
            )
            webChromeClient = RentalityWebChromeClient(
                onRequestPermission = {
                    onPermissionResult.launch(Manifest.permission.CAMERA)
                }
            )
            loadUrl(RENTALITY_URL)
        }
    }

    when (webViewState.value) {
        WebViewState.LOADING -> Loader()
        WebViewState.LOADED -> AndroidRentalityWebView(webView = webView)

        WebViewState.ERROR -> ErrorPage(onReload = {
            webViewState.value = WebViewState.LOADING
            webView.reload()
        })
    }


    BackHandler {
        webView.goBack()
    }

}

@Composable
private fun AndroidRentalityWebView(
    webView: RentalityWebView,
) {
    AndroidView(
        modifier = Modifier, factory = {
            SwipeRefreshLayout(it).apply {
                setOnRefreshListener {
                    webView.reload()
                    isRefreshing = false
                }
                webView.parent?.let { parent ->
                    (parent as? ViewGroup)?.removeView(webView)
                }
                addView(webView)
            }
        })
}

@Composable
private fun ErrorPage(onReload: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180937)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_internet),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(text = stringResource(R.string.check_internet), color = Color.White)
            Button(
                modifier = Modifier.padding(top = 16.dp).width(120.dp),
                onClick = onReload
            ) {
                Text(text = stringResource(R.string.try_again), color = Color.White)
            }
            Image(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(300.dp),
                painter = painterResource(R.drawable.car_404), contentDescription = null
            )
        }
    }
}

@Composable
private fun Loader() {

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180937)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = R.drawable.platform_loader)
                    .apply(block = {
                        size(Size.ORIGINAL)
                    }).build(), imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier.size(280.dp),
        )
    }
}