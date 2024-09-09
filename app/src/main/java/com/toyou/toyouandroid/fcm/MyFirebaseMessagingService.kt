package com.toyou.toyouandroid.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.presentation.base.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val fcmRepository = FCMRepository()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "FCM 토큰: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenRequest = Token(token)
                fcmRepository.postToken(tokenRequest)
                Log.d("sendTokenToServer", "성공")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("sendTokenToServer", "토큰 전송 실패: ${e.message}")
            }
        }

    }

    private fun deleteToken(token : String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenRequest = Token(token)
                fcmRepository.deleteToken(tokenRequest)
                Log.d("deleteToken", "성공")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("deleteToken", "토큰 삭제 실패: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 알림 메시지가 있을 경우 처리
        remoteMessage.notification?.let {
            val title = it.title ?: "No Title"
            val body = it.body ?: "No Body"

            // 받은 알림을 사용자에게 표시
            sendNotification(title, body)
        }
    }

    private fun sendNotification(title: String?, message: String?) {
        // 알림 채널 생성
        createNotificationChannel()

        // 알림 클릭 시 열릴 인텐트 설정 (MainActivity 예시)
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        // Add FLAG_IMMUTABLE for Android 12 and above
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 빌더
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.fcm) // 아이콘 설정 (적절한 아이콘으로 대체)
            .setContentTitle(title) // 알림 제목
            .setContentText(message) // 알림 내용
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 알림 우선순위
            .setAutoCancel(true) // 클릭 시 자동 제거
            .setContentIntent(pendingIntent) // 인텐트 연결

        Log.d("MyFirebaseMessagingService", "Notification Title: $title")
        Log.d("MyFirebaseMessagingService", "Notification Message: $message")

        // NotificationManager를 통해 알림을 생성
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // 중요도 설정
            )
            channel.enableLights(true)
            channel.enableVibration(true)

            // NotificationManager에 채널 등록
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_NAME = "FCM STUDY"
        private const val CHANNEL_ID = "FCM__channel_id"
    }

}