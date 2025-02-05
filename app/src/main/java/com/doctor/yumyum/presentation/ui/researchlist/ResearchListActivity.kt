package com.doctor.yumyum.presentation.ui.researchlist

import ResearchListAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.doctor.yumyum.R
import com.doctor.yumyum.common.base.BaseActivity
import com.doctor.yumyum.common.utils.ORDER_FLAG
import com.doctor.yumyum.common.utils.REQUEST_CODE
import com.doctor.yumyum.common.utils.SORT_FLAG
import com.doctor.yumyum.databinding.ActivityResearchListBinding
import com.doctor.yumyum.databinding.DialogSelectSearchBinding
import com.doctor.yumyum.databinding.DialogSelectSortBinding
import com.doctor.yumyum.presentation.adapter.TasteTagAdapter
import com.doctor.yumyum.presentation.ui.filter.FilterActivity
import com.doctor.yumyum.presentation.ui.recipedetail.RecipeDetailActivity
import com.doctor.yumyum.presentation.ui.search.hashtag.SearchHashtagActivity
import com.doctor.yumyum.presentation.ui.search.taste.SearchTasteActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.util.*

class ResearchListActivity :
    BaseActivity<ActivityResearchListBinding>(R.layout.activity_research_list) {

    private lateinit var sortDialog: BottomSheetDialog
    private lateinit var searchDialog: BottomSheetDialog
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ResearchListViewModel::class.java]
    }
    private var categoryName = ""
    private var sort = "id"
    private var order = "desc"
    private var minPrice: Int? = null
    private var maxPrice: Int? = null
    private var flavors: ArrayList<String> = arrayListOf("")
    private var tags: ArrayList<String> = arrayListOf("")
    private val researchListAdapter = ResearchListAdapter({ recipeId ->
        val intent = Intent(this, RecipeDetailActivity::class.java)
        intent.putExtra("recipeId", recipeId)
        detailLauncher.launch(intent)
    }, { recipe, position ->
        viewModel.setBookmarkState(recipe, position)
    }, {}, {})
    private val filterLauncher: ActivityResultLauncher<Intent> =
        // 필터 화면에서 돌아왔을 때
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                minPrice = it.data?.extras?.get("minPrice") as Int?
                maxPrice = it.data?.extras?.get("maxPrice") as Int?

                // 필터 아이콘 상태 변경
                if (minPrice == null && maxPrice == null) {
                    // 적용하지 않은 상태
                    binding.researchListTvFilter.setCompoundDrawablesWithIntrinsicBounds(
                        getDrawable(R.drawable.ic_filter_gray), null, null, null
                    )
                } else {
                    // 적용한 상태
                    binding.researchListTvFilter.setCompoundDrawablesWithIntrinsicBounds(
                        getDrawable(R.drawable.ic_filter_green), null, null, null
                    )
                }
                searchRecipeList()
            }
        }
    private val searchTasteLauncher: ActivityResultLauncher<Intent> =
        // 맛별로 검색할 때
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val tasteList = it.data?.extras?.getStringArrayList(TASTE_EXTRA_KEY)

                // 검색창 리스트 설정
                tasteList?.let { list ->
                    viewModel.setSearchList(list)
                    flavors = list
                    tags = arrayListOf("")
                }
                searchRecipeList()
            }
        }
    private val searchHashtagLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val hashtagList = it.data?.extras?.getStringArrayList(HASHTAG_EXTRA_KEY)

                // 검색창 리스트 설정
                hashtagList?.let { list ->
                    viewModel.setSearchList(ArrayList(list.map { it -> "#$it" }))
                    tags = list
                    flavors = arrayListOf("")
                }

                searchRecipeList()
            }
        }
    private val detailLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == REQUEST_CODE.DELETE_RECIPE) {
                searchRecipeList()
            }
        }
    private var touchStartTime: Long = Calendar.getInstance().timeInMillis

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryName = intent.extras?.get(getString(R.string.common_brand_en)).toString()
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.researchListTvBrand.text = categoryName
        binding.researchListRvSearchTaste.adapter = TasteTagAdapter()

        // recycler view touch listener
        binding.researchListRvSearchTaste.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                // 누를 때 시간 저장
                touchStartTime = Calendar.getInstance().timeInMillis
                true
            } else if (motionEvent.action == MotionEvent.ACTION_UP && Calendar.getInstance().timeInMillis - touchStartTime < MAX_CLICK_DURATION) {
                // 뗄 때 시간과 비교해서 차이가 DURATION 보다 작으면 다이얼로그 띄우기
                showSearchDialog()
                true
            } else {
                false
            }
        }
        initSortDialog()
        initSearchDialog()

        // 필터 화면으로 이동
        binding.researchListTvFilter.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            intent.putExtra("minPrice", minPrice)
            intent.putExtra("maxPrice", maxPrice)
            filterLauncher.launch(intent)
        }
        binding.researchListClAppbar.appbarIbBack.setOnClickListener { finish() }
        binding.researchListRvRecipe.adapter = researchListAdapter

        // 정렬 타입 변화 확인
        viewModel.sortType.observe(this) { type ->
            var src = getDrawable(R.drawable.ic_sort_green)

            when (type) {
                ResearchListViewModel.SORT_RECENT -> {
                    sort = SORT_FLAG.ID
                    order = ORDER_FLAG.DESC
                    src = getDrawable(R.drawable.ic_sort_gray)
                }
                ResearchListViewModel.SORT_LIKE -> {
                    sort = SORT_FLAG.LIKE
                    order = ORDER_FLAG.DESC
                }
                ResearchListViewModel.SORT_EXPENSIVE -> {
                    sort = SORT_FLAG.PRICE
                    order = ORDER_FLAG.DESC
                }
                ResearchListViewModel.SORT_CHEAP -> {
                    sort = SORT_FLAG.PRICE
                    order = ORDER_FLAG.ASC
                }
            }
            sortDialog.dismiss()
            searchRecipeList()

            // 정렬 아이콘 상태 변경
            binding.researchListTvSort.setCompoundDrawablesWithIntrinsicBounds(
                src, null, null, null
            )
        }

        viewModel.searchType.observe(this) {
            if (it == ResearchListViewModel.SEARCH_HASHTAG) {
                searchHashtagLauncher.launch(Intent(this, SearchHashtagActivity::class.java))
            } else if (it == ResearchListViewModel.SEARCH_TASTE) {
                searchTasteLauncher.launch(Intent(this, SearchTasteActivity::class.java))
            }
            searchDialog.dismiss()
        }

        viewModel.recipeList.observe(this) {
            researchListAdapter.setRecipeList(it)
        }
        viewModel.errorState.observe(this) { resId ->
            showToast(getString(resId))
        }
        // 레시피 상태 변화 시
        viewModel.recipePosition.observe(this) { position ->
            researchListAdapter.notifyItemChanged(position)
        }
    }

    private fun initSortDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.dialog_select_sort, null)
        val bottomSheetBinding = DataBindingUtil.inflate<DialogSelectSortBinding>(
            layoutInflater,
            R.layout.dialog_select_sort,
            bottomSheetView as ViewGroup,
            false
        )
        bottomSheetBinding.lifecycleOwner = this
        bottomSheetBinding.viewModel = viewModel
        sortDialog = BottomSheetDialog(this)
        sortDialog.setContentView(bottomSheetBinding.root)
    }

    fun showSortDialog() {
        sortDialog.show()
        viewModel.initSortType()
    }

    private fun initSearchDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.dialog_select_search, null)
        val bottomSheetBinding = DataBindingUtil.inflate<DialogSelectSearchBinding>(
            layoutInflater,
            R.layout.dialog_select_search,
            bottomSheetView as ViewGroup,
            false
        )
        bottomSheetBinding.lifecycleOwner = this
        bottomSheetBinding.viewModel = viewModel
        searchDialog = BottomSheetDialog(this)
        searchDialog.setContentView(bottomSheetBinding.root)
    }

    fun showSearchDialog() {
        searchDialog.show()
        viewModel.initSearchType()
    }

    private fun searchRecipeList() {
        lifecycleScope.launch {
            viewModel.searchRecipeList(
                categoryName,
                flavors,
                tags,
                minPrice,
                maxPrice,
                sort,
                order,
                "2022-12-26T12:12:12",
                0,
                100
            )
        }
    }

    fun resetSearchList() {
        viewModel.setSearchList(arrayListOf())
        // 맛 리스트 초기화
        flavors = arrayListOf("")
        // 해시태그 리스트 초기화
        tags = arrayListOf("")

        searchRecipeList()
    }

    companion object {
        const val MAX_CLICK_DURATION = 100
        const val TASTE_EXTRA_KEY = "taste list"
        const val HASHTAG_EXTRA_KEY = "hashtag list"
    }
}
