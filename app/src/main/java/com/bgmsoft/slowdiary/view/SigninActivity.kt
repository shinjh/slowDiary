package com.bgmsoft.slowdiary.view

import android.os.Bundle
import com.bgmsoft.slowdiary.R
import com.bgmsoft.slowdiary.view.components.BaseActivity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.bgmsoft.slowdiary.constants.Constants
import com.bgmsoft.slowdiary.databinding.ActivitySigninBinding
import com.bgmsoft.slowdiary.utils.L
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.kakao.util.exception.KakaoException
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeResponseCallback
import com.kakao.usermgmt.response.model.UserProfile
import com.kakao.util.helper.log.Logger
import android.os.AsyncTask
import com.bgmsoft.slowdiary.exts.plusAssign
import com.bgmsoft.slowdiary.view.components.AutoClearedDisposable
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_signin.*
import java.io.IOException
import java.lang.ref.WeakReference


class SigninActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var callback: SessionCallback? = null
    private var mGoogleSigninClient: GoogleSignInClient? = null
    private val RC_GOOGLE_SIGN_IN = 1000

    private val viewDisposables = AutoClearedDisposable(this)
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle += viewDisposables

        // layout binding
        binding = DataBindingUtil.setContentView<ActivitySigninBinding>(this, R.layout.activity_signin).apply {
            this.lifecycleOwner = this@SigninActivity
        }

        // 카카오 세션 콜백 초기화
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
//        Session.getCurrentSession().checkAndImplicitOpen()

        // Google 로그인 버튼 동작
        google_sign_in_button.setOnClickListener {
            // Google
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSigninClient = GoogleSignIn.getClient(this, gso)

            google_sign_in_button.performClick()
            mGoogleSigninClient?.run {
                try {
                    startActivityForResult(this.signInIntent, RC_GOOGLE_SIGN_IN)
                } catch (e: Exception) {
                    L.e(e.toString())
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // 카카오 세션 콜백 제거
        Session.getCurrentSession().removeCallback(callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    // Google 로그인 실패 처리
    override fun onConnectionFailed(result: ConnectionResult) {
        L.e("Google onConnectionFailed:${result.errorCode}")
    }

    // Google 로그인 성공 처리
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val scopes = "oauth2:${Scopes.PLUS_ME}"

            GoogleTokenAsyncTask(this).execute(account, scopes)

            L.e("Google signInResult:success")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            L.e("Google signInResult:failed code=" + e.statusCode)
        }
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

    private class GoogleTokenAsyncTask internal constructor(context: SigninActivity) :
        AsyncTask<Any, Void, Void>() {

        private val activityReference: WeakReference<SigninActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Any): Void? {
            val activity = activityReference.get()
            val account: GoogleSignInAccount = params[0] as GoogleSignInAccount
            val scopes: String = params[1] as String

            try {
                GoogleAuthUtil.getToken(activityReference.get(), account.account, scopes)?.let {
                    L.e("Google Access Token: $it")
                    activity?.requestSigninSocial(
                        account.id.toString(),
                        account.displayName.toString(),
                        Constants.Social.GOOGLE.name,
                        it
                    )
                }
            } catch (e: UserRecoverableAuthException) {
                L.w("Google Error retrieving the token: ${e.message}")
                L.d("Trying to solve the problem...")
                activity?.startActivityForResult(e.intent, 1)
            } catch (e: IOException) {
                L.e("Google Unrecoverable I/O exception: ${e.message}")
            } catch (e: GoogleAuthException) {
                L.e("Google Unrecoverable authentication exception: ${e.message}")
            } catch (e: Throwable) {
                L.e("Google ${e.message}")
            }

            return null
        }
    }
}
