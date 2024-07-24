package com.toyou.toyouandroid

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class ToYouApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.kakao_NATIVE_APP_KEY)
    }
}