package com.toyou.toyouandroid.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.utils.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {


    private lateinit var tokenStorage: TokenStorage

    override fun onCreate() {
        super.onCreate()
        // TokenStorage 초기화
        tokenStorage = TokenStorage(applicationContext)


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "FCM 토큰: $token")

        tokenStorage.saveFcmToken(token)
        Log.d("FCM Token 저장", "토큰이 저장되었습니다: $token")

        FirebaseMessaging.getInstance().subscribeToTopic("alarm")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "topic 구독 성공'")
                } else {
                    Log.e("FCM",task.exception.toString())
                }
            }

        /*FirebaseMessaging.getInstance().unsubscribeFromTopic("alarm")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "topic 구독 취소 성공")
                } else {
                    Log.e("FCM", task.exception.toString())
                }
            }*/

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            remoteMessage.notification?.let {
                sendNotification(it.title, it.body)
            }
        } else {
            Log.d("FCM", "알림 권한이 없어 알림을 표시할 수 없습니다.")
        }
    }

    private fun sendNotification(title: String?, message: String?) {
        // 알림 채널 생성
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

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