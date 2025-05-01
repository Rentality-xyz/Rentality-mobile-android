package com.eigen.rentality.webview

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberPermissionLauncher(context: Context, onCameraResult: (Intent) -> Unit,) = rememberLauncherForActivityResult(
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
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        RentalityWebChromeClient.uri = FileProvider.getUriForFile(
            context,
            context.packageName, fileTemp
        )
        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, RentalityWebChromeClient.uri)

        val putter = Intent(Intent.ACTION_CHOOSER)
        putter.putExtra(Intent.EXTRA_INTENT, getContent)
        putter.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(camIntent))
        onCameraResult(putter)
    }
}