package com.doctor.yumyum.presentation.ui.login

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.doctor.yumyum.R
import com.doctor.yumyum.common.base.BaseActivity
import com.doctor.yumyum.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewmodel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[LoginViewModel::class.java]

        //binding
        binding.apply {
            lifecycleOwner = this@LoginActivity
            viewModel = viewModel
            loginBtnKakao.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    kakaoSignUp()
                }
            }
            loginBtnGoogle.setOnClickListener {
                // TODO :: 준영
            }
        }

        // 회원가입 에러 처리
        viewModel.errorState.observe(this) {
            if (it == true) {
                ErrorDialog().apply {
                    show(supportFragmentManager, "ErrorDialog")
                }
            }
        }
    }

    suspend fun kakaoSignUp() {
        viewModel.setOauthType("KAKAO")

        try {
            val accessToken = kakaoLogin()
            val nickname = kakaoUserInfo()
            viewModel.setAccessToken(accessToken)
            viewModel.setNickname(nickname)

        } catch (e: Exception) {
            ErrorDialog().apply {
                show(supportFragmentManager, "ErrorDialog")
            }
        }
        viewModel.signUp()
    }

    private suspend fun kakaoLogin() = suspendCancellableCoroutine<String> { cont ->
        // 로그인 callback
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            // 로그인 여부
            if (error == null) {
                if (token != null) {
                    cont.resume(token.accessToken)
                }
            }
            else {
                cont.resumeWithException(Throwable())
            }
        }

        LoginClient.instance.run {
            // 카카오톡 있을때
            if (this.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                this.loginWithKakaoTalk(this@LoginActivity, callback = callback)
            }
            // 카카오톡 없을때
            else {
                this.loginWithKakaoAccount(this@LoginActivity, callback = callback)
            }
        }
    }

    private suspend fun kakaoUserInfo() = suspendCancellableCoroutine<String> { cont ->
        // 사용자 정보 요청 여부
        UserApiClient.instance.me { user, error ->
            if (error == null) {
                if (user?.kakaoAccount?.profile?.nickname != null) {
                    cont.resume(user.kakaoAccount?.profile?.nickname.toString())
                }
            }
            else {
                cont.resumeWithException(Throwable())
            }
        }
    }

}