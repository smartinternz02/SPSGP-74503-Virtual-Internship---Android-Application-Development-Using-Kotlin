package com.himanshu.android.view.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.himanshu.android.databinding.FragmentPaymentMethodBinding
import com.himanshu.android.view.ui.base.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding>(
    FragmentPaymentMethodBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = findNavController()
        binding.appBarLayout.toolbar.setupToolbar()

        binding.cashConstraint.setOnClickListener {
            mainActivityViewModel.paymentMethod.postValue("Cash On Delivery")
            navController.popBackStack()
        }

        binding.gcashConstraint.setOnClickListener {
            mainActivityViewModel.paymentMethod.postValue("GCash")
            navController.popBackStack()
        }
    }
}