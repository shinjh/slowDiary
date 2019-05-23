package com.bgmsoft.slowdiary

import android.app.Activity
import android.app.Application
import android.content.Context
import com.kakao.auth.*
import com.kakao.auth.KakaoSDK
import com.kakao.auth.IApplicationConfig


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        context = this

        KakaoSDK.init(KakaoSDKAdapter())
    }

    private class KakaoSDKAdapter : KakaoAdapter() {

        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        override fun getSessionConfig(): ISessionConfig {
            return object : ISessionConfig {

                override fun getAuthTypes(): Array<AuthType> = arrayOf(AuthType.KAKAO_LOGIN_ALL)

                override fun isUsingWebviewTimer(): Boolean = false

                override fun getApprovalType(): ApprovalType = ApprovalType.INDIVIDUAL

                override fun isSaveFormData(): Boolean = true
            }
        }

        override fun getApplicationConfig(): IApplicationConfig {
            return object : IApplicationConfig {
                override fun getTopActivity(): Activity {
                    return App.topActivity
                }

                override fun getApplicationContext(): Context {
                    return getGlobalApplicationContext()
                }
            }
        }
    }

    companion object {
        private lateinit var context: Context
        private lateinit var topActivity: Activity

        fun getGlobalApplicationContext() = context
    }
}