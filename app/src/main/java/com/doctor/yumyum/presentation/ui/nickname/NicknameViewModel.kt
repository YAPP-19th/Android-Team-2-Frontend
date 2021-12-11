package com.doctor.yumyum.presentation.ui.nickname

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doctor.yumyum.common.base.BaseViewModel
import com.doctor.yumyum.data.model.NicknamePatchModel
import com.doctor.yumyum.data.remote.response.GetNicknameResponse
import com.doctor.yumyum.data.repository.UserRepositoryImpl
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Exception

class NicknameViewModel : BaseViewModel() {
    private val userRepository = UserRepositoryImpl()
    private val _errorState: MutableLiveData<Boolean> = MutableLiveData(false)
    val errorState: LiveData<Boolean>
        get() = _errorState
    val nickname: MutableLiveData<String> = MutableLiveData("")

    suspend fun getNickname() {
        try {
            val nicknameResponse: Response<GetNicknameResponse> = userRepository.getNicknameApi()
            nickname.postValue(nicknameResponse.body()?.nickname)

        } catch (e: Exception) {
            _errorState.postValue(true)
        }
    }

    suspend fun validateNickname(nickname: String): Boolean {
        return try {
            val nicknameResponse: Response<ResponseBody> =
                userRepository.validateNicknameApi(nickname)
            nicknameResponse.code() == 200

        } catch (e: Exception) {
            false
        }
    }

    suspend fun patchNickname() {
        val nicknameResponse: Response<ResponseBody>? =
            nickname.value?.let { NicknamePatchModel(it) }?.let {
                userRepository.patchNicknameApi(
                    it
                )
            }
    }
}