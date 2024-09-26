package com.eigen.rentality.webview

import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class RentalityWebViewClient(
    private val onPageFinished: () -> Unit
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.loadUrl(JS_PDF_READER.trimIndent())
        onPageFinished()
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

private const val JS_PDF_READER = """
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
                    """