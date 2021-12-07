package com.doctor.yumyum.presentation.ui.write

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import com.doctor.yumyum.R
import com.doctor.yumyum.common.base.BaseActivity
import com.doctor.yumyum.databinding.ActivityWriteTagBinding
import com.doctor.yumyum.presentation.adapter.WriteTagAdapter
import com.doctor.yumyum.presentation.ui.write.viewmodel.WriteTagViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

/**
 *  레시피 재료 입력
 *  - 추가된 재료 & 빼는 재료 해쉬태그로 입력하는 화면
 */

class WriteTagActivity : BaseActivity<ActivityWriteTagBinding>(R.layout.activity_write_tag) {

    private var removeMode = DELETE_STATUS
    companion object {
        const val DELETE_STATUS = 1000
        const val SELECT_DELETE_STATUS = 1001
    }

    private var removeItemList = arrayListOf<String>()
    private val tagViewModel: WriteTagViewModel by viewModels()
    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initTagRecycler()
        getRequestCode()

        binding.writeTagEtInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                tagViewModel.validTagItem()
                binding.writeTagEtInput.text.clear()
            }
            false
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.tagActivity = this
        binding.tagViewModel = tagViewModel
    }

    private fun initTagRecycler() {
        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }.let { layoutManager ->
            binding.writeTagRvInput.layoutManager = layoutManager
            binding.writeTagRvInput.adapter = WriteTagAdapter {
                if(removeMode == SELECT_DELETE_STATUS){
                    if(removeItemList.contains(it)){
                        //TODO: 배경색&글자색 변경
                        removeItemList.remove(it)
                    }else{
                        //TODO: 배경색&글자색 변경
                        removeItemList.add(it)
                    }
                }
            }
        }
    }

    private fun getRequestCode() {
        requestCode = intent.extras?.getInt(resources.getString(R.string.write_tag_type)) ?: 0
        val guideText: String = when (requestCode) {
            WriteFragment2.REQUEST_CODE_ADD_INGREDIENTS -> resources.getString(R.string.write_tv_add)
            WriteFragment2.REQUEST_CODE_MINUS_INGREDIENTS -> resources.getString(R.string.write_tv_minus)
            else -> ""
        }
        binding.writeTagEtInput.hint = guideText
        binding.writeTagTvGuide.text = guideText
    }

    fun removeClickListener() {
        when (removeMode) {
            DELETE_STATUS -> {
                removeMode = SELECT_DELETE_STATUS
                binding.writeTagTvRemove.text = resources.getString(R.string.write_tag_select_delete)
                binding.writeTagTvRemove.setTextColor(resources.getColor(R.color.main_orange))
            }
            SELECT_DELETE_STATUS -> {
                removeMode = DELETE_STATUS
                binding.writeTagTvRemove.text = resources.getString(R.string.write_tag_delete)
                binding.writeTagTvRemove.setTextColor(resources.getColor(R.color.black))

                removeItemList.forEach {
                    tagViewModel.removeTagItem(it)
                }
                removeItemList.clear()
            }
        }
    }

    fun finishInput() {
        val intent = Intent(this, WriteFragment2::class.java)
        intent.putStringArrayListExtra("inputList", tagViewModel.tagListLiveData.value)
        setResult(requestCode, intent)
        finish()
    }

    fun cancleInput() {
        finish()
    }
}