package com.example.safepickup.Firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}