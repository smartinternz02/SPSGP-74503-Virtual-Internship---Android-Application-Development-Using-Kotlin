package com.himanshu.android.view.ui.browse

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.himanshu.android.R
import com.himanshu.android.databinding.FragmentMerchantSearchBinding
import com.himanshu.android.domain.db.search.SearchEntity
import com.himanshu.android.utils.ClickType
import com.himanshu.android.utils.Constants
import com.himanshu.android.utils.Utils.showSoftKeyboard
import com.himanshu.android.view.state.ViewState
import com.himanshu.android.view.ui.base.BaseFragment
import com.himanshu.android.view.ui.browse.adapter.SearchAdapter
import com.himanshu.android.view.ui.home.FavoriteFragmentDirections
import com.himanshu.android.view.ui.home.adapter.MerchantAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.ArrayList

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MerchantSearchFragment : BaseFragment<FragmentMerchantSearchBinding>(
    FragmentMerchantSearchBinding::inflate
) {
    private val searchAdapter by lazy { SearchAdapter() }
    private val merchantAdapter by lazy { MerchantAdapter() }
    private val args by navArgs<MerchantSearchFragmentArgs>()
    private var isSearchRvVisible: Boolean = false
    private var currentSearchTerm: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchView()
        observeMerchantsLiveData()

        binding.merchantRv.adapter = merchantAdapter.apply {setUser(authViewModel.userRecord.value)}
        binding.searchRv.adapter = searchAdapter

        val args = args.searchTerm
        if (!args.isNullOrBlank()) {
            currentSearchTerm = args
            binding.searchView.setQuery(args, false)
            binding.title.visibility = View.GONE
            binding.searchView.clearFocus()
            mainViewModel.queryMerchants(Constants.SEARCH, args)
        } else {
            getAllSearch()
        }

        searchAdapter.listener = { type, search, _ ->
            when (type) {
                ClickType.NEGATIVE -> {
                    val searchTerm = search.searchTerm
                    currentSearchTerm = searchTerm
                    binding.searchView.setQuery(searchTerm, false)
                    binding.title.visibility = View.GONE
                    binding.merchantsShimmerLayout.visibility = View.GONE
                    binding.merchantsShimmerLayout.stopShimmer()
                    binding.searchView.clearFocus()
                    mainViewModel.queryMerchants(Constants.SEARCH, searchTerm)
                }
                ClickType.POSITIVE -> {
                    browseViewModel.deleteSearch(search)
                    getAllSearch()
                }
            }
        }

        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.commonNoticeLayout.hideNotice()
                binding.merchantRv.visibility = View.GONE
                getAllSearch()
                showSoftKeyboard(binding.searchView)
            }
        }

        merchantAdapter.apply {
            onFavoriteClicked = { merchant, position, direction ->
                homeViewModel.favorite(merchant._id, direction)
                observeFavorite(this, merchant, position, direction)
            }
        }

        merchantAdapter.onMerchantClicked = { merchant ->
            findNavController().navigate(
                FavoriteFragmentDirections.actionGlobalToMerchantFragment(merchant._id)
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getAllSearch() {
        browseViewModel.getAllSearch { recentSearch ->
            if (!recentSearch.isNullOrEmpty()) {
                isSearchRvVisible = true
                binding.searchRv.visibility = View.VISIBLE
                binding.title.apply {
                    text = getString(R.string.recent_searches)
                    visibility = View.VISIBLE
                }
                searchAdapter.submitList(recentSearch)
            }
        }
    }

    private fun initSearchView() {
        val searches = arrayListOf<SearchEntity>()
        binding.searchRv.visibility = View.VISIBLE
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query?.length ?: 0 < 3) {
                        showShortToast(R.string.search_minimum)
                        return false
                    }
                    binding.title.visibility = View.GONE
                    binding.merchantsShimmerLayout.visibility = View.GONE
                    binding.merchantsShimmerLayout.stopShimmer()
                    currentSearchTerm = query
                    mainViewModel.queryMerchants(Constants.SEARCH, query)
                    clearFocus()
                    val date = System.currentTimeMillis()
                    browseViewModel.insertSearch(SearchEntity(query ?: "", true, date))
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.title.text = getString(R.string.search_for, newText)
                    if (newText.isNullOrBlank()) {
                        getAllSearch()
                    } else {
                        if (searches.isEmpty()) {
                            searches.add(SearchEntity("Burger"))
                            searches.add(SearchEntity("Chicken"))
                            searches.add(SearchEntity("Chicken fillet"))
                            searchAdapter.submitList(searches)
                        }
                    }
                    val filterSearches =
                        searches.filter {
                            it.searchTerm.contains(newText ?: "", ignoreCase = true)
                        }
                    searchAdapter.submitList(ArrayList(filterSearches))
                    return false
                }
            })
        }
    }

    private fun observeMerchantsLiveData() {
        mainViewModel.merchants.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    isSearchRvVisible = false
                    binding.searchRv.visibility = View.GONE
                    binding.merchantRv.visibility = View.GONE
                    binding.commonNoticeLayout.hideNotice()
                    binding.merchantsShimmerLayout.visibility = View.VISIBLE
                    binding.merchantsShimmerLayout.startShimmer()
                }
                is ViewState.Success -> {
                    val merchants = response.result.data
                    if (merchants.isNullOrEmpty()) {
                        binding.commonNoticeLayout.showNotice(
                            R.drawable.ic_empty_result,
                            R.string.empty_result,
                            R.string.empty_result_message,
                            currentSearchTerm,
                            null,
                            false
                        ) {}
                    } else {
                        binding.title.apply {
                            val resultSize = merchants.size
                            text = if(resultSize > 1)
                                getString(R.string.search_results, resultSize, currentSearchTerm)
                            else getString(R.string.search_result, currentSearchTerm)
                            visibility = View.VISIBLE
                        }
                        binding.merchantRv.visibility = View.VISIBLE
                        merchantAdapter.submitList(merchants)
                    }
                    binding.merchantsShimmerLayout.visibility = View.GONE
                    binding.merchantsShimmerLayout.stopShimmer()
                }
                is ViewState.Error -> {
                    binding.merchantRv.visibility = View.GONE
                    binding.merchantsShimmerLayout.visibility = View.GONE
                    binding.merchantsShimmerLayout.stopShimmer()
                    if (!isSearchRvVisible) {
                        binding.commonNoticeLayout.showNetworkError {
                            mainViewModel.queryMerchants(Constants.SEARCH, currentSearchTerm)
                        }
                    }
                }
            }
        }
    }
}