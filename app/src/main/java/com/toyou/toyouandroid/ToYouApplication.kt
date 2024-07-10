package com.toyou.toyouandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** HiltAndroidApp 컴포넌트 생성
 *
 * AndroidEntrypoint로 그에 대한 하위 컴포넌트를 생성 후 DI 컨테이너 주입 */
@HiltAndroidApp
class ToYouApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}