package com.doctor.yumyum.presentation.ui.researchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doctor.yumyum.common.base.BaseViewModel

class ResearchListViewModel : BaseViewModel() {
    private var _sortType: MutableLiveData<Int> = MutableLiveData(SORT_RECENT)
    val sortType: LiveData<Int> get() = _sortType
    private var _tmpSortType: MutableLiveData<Int> = MutableLiveData()
    val tmpSortType: LiveData<Int> get() = _tmpSortType

    fun initSortType() {
        _tmpSortType.value = sortType.value
    }

    fun setTmpSortType(type: Int) {
        _tmpSortType.value = type
    }

    fun setSortType() {
        _sortType.value = tmpSortType.value
    }

    companion object {
        const val SORT_RECENT = 1
        const val SORT_SCRAP = 2
        const val SORT_LIKE = 3
        const val SORT_EXPENSIVE = 4
        const val SORT_CHEAP = 5
    }
}