package com.eigen.rentality.webview

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberCameraLauncher() = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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