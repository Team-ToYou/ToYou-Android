package com.toyou.toyouandroid.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toyou.core.common.AppConstants
import com.toyou.core.datastore.NotificationPreferences
import com.toyou.core.datastore.TokenStorage
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.ui.base.MainActivity
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MyFirebaseMessagingServiceEntryPoint {
        fun tokenStorage(): TokenStorage
        fun notificationPreferences(): NotificationPreferences
    }

    private val tokenStorage: TokenStorage by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            MyFirebaseMessagingServiceEntryPoint::class.java
        ).tokenStorage()
    }

    private val notificationPreferences: NotificationPreferences by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            MyFirebaseMessagingServiceEntryPoint::class.java
        ).notificationPreferences()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM token received")
        // FCM 서비스 콜백이므로 blocking call 사용
        @Suppress("DEPRECATION")
        tokenStorage.getAccessTokenBlocking() // TokenStorage 초기화 확인용

        CoroutineScope(Dispatchers.IO).launch {
            try {
                tokenStorage.saveFcmToken(token)
                Timber.d("FCM token saved successfully")

                // 구독 여부 확인 후 구독
                if (notificationPreferences.isSubscribedBlocking()) {
                    subscribeToTopic()
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save FCM token")
            }
        }
    }

    fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConstants.Fcm.TOPIC_ALL_USERS)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Topic subscription successful")
                    @Suppress("DEPRECATION")
                    notificationPreferences.setSubscribedBlocking(true)
                } else {
                    Timber.e(task.exception, "Topic subscription failed")
                }
            }
    }

    fun unsubscribeFromTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConstants.Fcm.TOPIC_ALL_USERS)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Topic unsubscription successful")
                    @Suppress("DEPRECATION")
                    notificationPreferences.setSubscribedBlocking(false)
                } else {
                    Timber.e(task.exception, "Topic unsubscription failed")
                }
            }
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
            Timber.tag("FCM").d("알림 권한이 없어 알림을 표시할 수 없습니다.")
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
        val notificationBuilder = NotificationCompat.Builder(this, AppConstants.Fcm.CHANNEL_ID)
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
        val channel = NotificationChannel(
            AppConstants.Fcm.CHANNEL_ID,
            AppConstants.Fcm.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH // 중요도 설정
        )
        channel.enableLights(true)
        channel.enableVibration(true)

        // NotificationManager에 채널 등록
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
