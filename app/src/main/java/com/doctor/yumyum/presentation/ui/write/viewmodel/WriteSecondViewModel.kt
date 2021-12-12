package com.doctor.yumyum.presentation.ui.write.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doctor.yumyum.common.base.BaseViewModel

class WriteSecondViewModel : BaseViewModel(){
    private val _addTagList : MutableLiveData<ArrayList<String>> = MutableLiveData()
    val addTagList : LiveData<ArrayList<String>>
        get() = _addTagList

    private val _minusTagList : MutableLiveData<ArrayList<String>> = MutableLiveData()
    val minusTagList : LiveData<ArrayList<String>>
        get() = _minusTagList

    fun setAddTagItem(newTagList: ArrayList<String>?){
        _addTagList.value = newTagList
        Log.d("Write2ViewModel",_addTagList.value.toString())
    }

    fun setMinusTagItem(newTagList: ArrayList<String>?){
        _minusTagList.value = newTagList
    }
}