package com.doctor.yumyum.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.doctor.yumyum.R
import com.doctor.yumyum.common.base.BaseActivity
import com.doctor.yumyum.databinding.ActivitySplashBinding
import com.doctor.yumyum.presentation.ui.login.LoginActivity
import com.doctor.yumyum.presentation.ui.main.MainActivity
<<<<<<< HEAD
import com.doctor.yumyum.presentation.ui.taste.TasteActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
=======
import com.doctor.yumyum.presentation.ui.nickname.NicknameActivity
>>>>>>> 74a583ec7a4a21b11e2b4e19a55e936dd9a60f25
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private lateinit var viewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewmodel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[SplashViewModel::class.java]

        //binding
        binding.apply {
            lifecycleOwner = this@SplashActivity
            viewModel = viewModel
        }
        startActivity(Intent(this, TasteActivity::class.java))

//        if (viewModel.loginToken.isNullOrBlank()) {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//        else {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
    }
}