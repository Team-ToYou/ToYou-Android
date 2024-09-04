package com.toyou.toyouandroid.fcm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.dto.request.Token
import com.toyou.toyouandroid.model.PreviewCardModel
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

    private fun retrieveTokenFromServer(name : String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fcmRepository.getToken(name)
                if (response.isSuccess) {
                    val serverToken = response.result.tokens
                    // 서버에서 받은 토큰을 사용해 로직을 처리
                    Log.d("Token Retrieval", serverToken.toString())
                } else {
                    Log.e("Token Retrieval", "토큰 조회 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("Token Retrieval", "토큰 조회 중 오류 발생: ${e.message}")

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
        super.onMessageReceived(remoteMessage)
        Log.d("FCM Message", "메시지 수신: ${remoteMessage.data}")
    }
}