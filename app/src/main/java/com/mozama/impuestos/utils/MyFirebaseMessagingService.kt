package com.mozama.impuestos.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Looper.prepare()
        Handler().post{
            Toast.makeText(baseContext, remoteMessage.notification?.title, Toast.LENGTH_SHORT).show()
        }
        Looper.loop()

        super.onMessageReceived(remoteMessage)
    }
}