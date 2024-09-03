package com.toyou.toyouandroid.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "FCM 토큰: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        // 이 메서드에서 서버에 토큰을 전송하는 코드를 구현

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM Message", "메시지 수신: ${remoteMessage.data}")
    }
}