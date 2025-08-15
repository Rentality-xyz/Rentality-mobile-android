package com.eigen.rentality

import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessagingService

const val PUSH_TOKEN = "PushToken"

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        getSharedPreferences(PUSH_TOKEN,0).edit { putString(PUSH_TOKEN, token) }
    }

}