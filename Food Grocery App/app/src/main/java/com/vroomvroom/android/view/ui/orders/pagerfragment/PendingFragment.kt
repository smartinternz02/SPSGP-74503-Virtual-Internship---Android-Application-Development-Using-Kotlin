package com.himanshu.android.view.ui.orders.pagerfragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.himanshu.android.databinding.FragmentPendingBinding
import com.himanshu.android.utils.Utils.safeNavigate
import com.himanshu.android.view.state.ViewState
import com.himanshu.android.view.ui.base.BaseFragment
import com.himanshu.android.view.ui.orders.OrdersFragmentDirections
import com.himanshu.android.view.ui.orders.adapter.OrderAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PendingFragment : BaseFragment<FragmentPendingBinding>(
    FragmentPendingBinding::inflate
) {

    private val orderAdapter by lazy { OrderAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ordersRv.adapter = orderAdapter
        observeOrdersByStatusLiveData()
        observeIsRefreshed()
        ordersViewModel.queryOrdersByStatus("Pending")

        orderAdapter.onMerchantClicked = { merchant ->
            if (merchant._id.isNotBlank()) {
                findNavController().safeNavigate(
                    OrdersFragmentDirections.actionGlobalToMerchantFragment(merchant._id)
                )
            }
        }
        orderAdapter.onOrderClicked = { orderId ->
            findNavController().navigate(
                OrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(orderId)
            )
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            ordersViewModel.queryOrdersByStatus("Pending")
            mainActivityViewModel.isRefreshed.postValue(true)
        }
    }

    private fun observeOrdersByStatusLiveData() {
        ordersViewModel.ordersByStatus.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.ordersRv.visibility = View.GONE
                    binding.commonNoticeLayout.hideNotice()
                    binding.shimmerLayout.startShimmer()
                    binding.shimmerLayout.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    val orders = response.result
                    if (orders.isEmpty()) {
                        orderAdapter.submitList(emptyList())
                        binding.commonNoticeLayout.showEmptyOrder {
                            findNavController().popBackStack() }
                    } else {
                        orderAdapter.submitList(orders)
                        binding.ordersRv.visibility = View.VISIBLE
                    }
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                is ViewState.Error -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.ordersRv.visibility = View.GONE
                    binding.commonNoticeLayout.showNetworkError {
                        ordersViewModel.queryOrdersByStatus("Pending") }
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private fun observeIsRefreshed() {
        mainActivityViewModel.isRefreshed.observe(viewLifecycleOwner) { refreshed ->
            if (refreshed) {
                ordersViewModel.queryOrdersByStatus("Pending")
            }
        }
    }
}