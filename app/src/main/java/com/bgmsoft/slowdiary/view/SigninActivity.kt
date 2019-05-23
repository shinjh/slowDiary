package com.bgmsoft.slowdiary.view

import android.os.Bundle
import com.bgmsoft.slowdiary.R
import com.bgmsoft.slowdiary.view.components.BaseActivity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.bgmsoft.slowdiary.constants.Constants
import com.bgmsoft.slowdiary.databinding.ActivitySigninBinding
import com.bgmsoft.slowdiary.utils.L
import com.kakao.util.exception.KakaoException
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.helper.log.Logger


class SigninActivity : BaseActivity() {

    private var callback: SessionCallback? = null

    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // layout binding
        binding = DataBindingUtil.setContentView<ActivitySigninBinding>(this, R.layout.activity_signin).apply {
            this.lifecycleOwner = this@SigninActivity
        }

        // 카카오 세션 콜백 초기화
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
//        Session.getCurrentSession().checkAndImplicitOpen()

    }

    override fun onDestroy() {
        super.onDestroy()

        // 카카오 세션 콜백 제거
        Session.getCurrentSession().removeCallback(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    // 소셜 로그인 요청
    private fun requestSigninSocial(userId: String, name: String, socialService: String, accessToken: String) {

    }

    private inner class SessionCallback : ISessionCallback {

        override fun onSessionOpened() {
            L.e("Kakao Session Opened")

            Session.getCurrentSession()?.accessToken?.run {
                UserManagement.requestMe(KakaoMeResponseCallback())
            }
        }

        override fun onSessionOpenFailed(exception: KakaoException?) {
            L.e("Kakao Session Open Failed")
            if (exception != null) {
                Logger.e(exception)
            }

            redirectSignupActivity()
        }
    }

    private fun redirectSignupActivity() {
        L.e("Kakao Session Redirect for sign up")
        Intent(this, SignupActivity::class.java).run { startActivity(this) }
        finish()
    }

    private inner class KakaoMeResponseCallback : MeResponseCallback() {
        override fun onSuccess(result: UserProfile?) {
            result?.run {
                L.e("kakao id : " + this.id.toString() + " // " + this.nickname)
                requestSigninSocial(
                    this.id.toString(),
                    this.nickname,
                    Constants.Social.KAKAO.name,
                    Session.getCurrentSession()?.accessToken!!
                )
            }
        }

        override fun onSessionClosed(errorResult: ErrorResult?) {
            L.e("Kakao Session Closed")
        }

        override fun onNotSignedUp() {
            L.e("Kakao Session not signed up")
        }
    }
}
