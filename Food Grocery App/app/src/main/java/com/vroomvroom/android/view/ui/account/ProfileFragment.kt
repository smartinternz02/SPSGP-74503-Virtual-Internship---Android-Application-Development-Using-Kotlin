package com.himanshu.android.view.ui.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.himanshu.android.R
import com.himanshu.android.databinding.FragmentProfileBinding
import com.himanshu.android.view.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        binding.appBarLayout.toolbar.setupToolbar()

        observeUser()

        binding.btnEditName.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_editBottomSheetFragment)
        }

        binding.btnEditPhone.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_phoneVerificationFragment)
        }

    }

    private fun observeUser() {
        authViewModel.userRecord.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.user = user
            }
        }
    }
}