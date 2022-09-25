package com.himanshu.android.view.ui.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.himanshu.android.R
import com.himanshu.android.databinding.FragmentEditBottomSheetBinding
import com.himanshu.android.utils.ClickType
import com.himanshu.android.view.state.ViewState
import com.himanshu.android.view.ui.base.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditBottomSheetFragment : BaseBottomSheetFragment<FragmentEditBottomSheetBinding>(
    FragmentEditBottomSheetBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUser()

        binding.btnSave.setOnClickListener {
            val name = binding.nameInputEditText.text
            if (name.toString().filter { !it.isWhitespace() }.isNotBlank()) {
                accountViewModel.mutationUpdateName(name.toString())
            }
        }
    }

    private fun observeUser() {
        accountViewModel.user.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.btnCancel.isEnabled = false
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    authViewModel.updateUserName(
                        response.result.updateName.id,
                        response.result.updateName.name!!
                    )
                    findNavController().popBackStack()
                }
                is ViewState.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.btnCancel.isEnabled = true
                    binding.progressIndicator.visibility = View.GONE
                    initAlertDialog()
                }
            }
        }
    }

    private fun initAlertDialog() {
        dialog.show(
            getString(R.string.network_error),
            getString(R.string.network_error_message),
            getString(R.string.cancel),
            getString(R.string.retry)
        ) { type ->
            when (type) {
                ClickType.POSITIVE -> {
                    val name = binding.nameInputEditText.text
                    if (name.toString().filter { !it.isWhitespace() }.isNotBlank()) {
                        accountViewModel.mutationUpdateName(name.toString())
                    }
                    dialog.dismiss()
                }
                ClickType.NEGATIVE -> dialog.dismiss()
            }
        }
    }
}