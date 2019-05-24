package com.bgmsoft.slowdiary.view

import android.os.Bundle
import com.bgmsoft.slowdiary.R
import com.bgmsoft.slowdiary.utils.EnvCheck
import android.content.Intent
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.bgmsoft.slowdiary.App
import com.bgmsoft.slowdiary.databinding.ActivitySplashBinding
import com.bgmsoft.slowdiary.view.components.BaseActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    private val DELAY_START_ACTIVITY = 500L

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // layout binding
        binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash).apply {
            this.lifecycleOwner = this@SplashActivity
        }

        // 구글 플레이 서비스 설치 확인 후 다음 동작을 진행한다
        if (EnvCheck.checkGooglePlayServices(this)) {
            checkSingIn()
        }
    }

    private fun checkSingIn() {
        // 1. 처음 실행 -> SigninActivity
        // 2. 처음 실행 X, 로그인 O -> MainActivity
        // 3. 처음 실행 X, 로그인 X -> SigninActivity

        when (App.prefs.prefsFirst) {
            true -> startSigninActivity()
            false -> App.prefs.prefsAuthToken?.let {
                startMainActivity()
            } ?: kotlin.run { startSigninActivity() }
        }
    }

    private fun startSigninActivity() {
        // 로그인 화면 실행
        Handler().postDelayed({ startActivityFinish<SigninActivity>() }, DELAY_START_ACTIVITY)
    }

    private fun startMainActivity() {
        // 메인 화면 실행
        Handler().postDelayed({ startActivityFinish<MainActivity>() }, DELAY_START_ACTIVITY)
    }
}
