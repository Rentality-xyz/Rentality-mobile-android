package com.eigen.rentality.firebase_notifications

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class RentalityFirebaseMessagingService(): FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("On new token is registered $token")
    }
}