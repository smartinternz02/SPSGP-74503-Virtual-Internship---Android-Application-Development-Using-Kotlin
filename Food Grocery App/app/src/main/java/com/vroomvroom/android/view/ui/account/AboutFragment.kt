package com.himanshu.android.view.ui.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.himanshu.android.R
import com.himanshu.android.databinding.FragmentAboutBinding
import com.himanshu.android.utils.Utils.safeNavigate
import com.himanshu.android.view.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AboutFragment : BaseFragment<FragmentAboutBinding>(
    FragmentAboutBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        binding.appBarLayout.toolbar.setupToolbar()

        binding.btnAboutUs.setOnClickListener {
            navigateOnClick(
                getString(R.string.about_us),
                getString(R.string.about_us_link)
            )
        }
        binding.btnTermsConditions.setOnClickListener {
            navigateOnClick(
                getString(R.string.terms_and_conditions),
                getString(R.string.terms_and_conditions_link)
            )
        }
        binding.btnPrivacyPolicy.setOnClickListener {
            navigateOnClick(
                getString(R.string.privacy_policy),
                getString(R.string.privacy_policy_link)
            )
        }

        binding.btnContactUs.setOnClickListener {
            navigateOnClick(
                getString(R.string.contact_us),
                getString(R.string.contact_us_link)
            )
        }
        binding.btnFaqs.setOnClickListener {
            navigateOnClick(
                getString(R.string.faqs),
                getString(R.string.faqs_link)
            )
        }
    }

    private fun navigateOnClick(title: String, url: String) {
        navController.safeNavigate(
            AboutFragmentDirections.actionAboutFragmentToWebViewFragment(title, url)
        )
    }
}