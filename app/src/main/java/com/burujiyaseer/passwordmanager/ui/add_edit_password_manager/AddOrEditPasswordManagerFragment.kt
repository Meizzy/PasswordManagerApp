package com.burujiyaseer.passwordmanager.ui.add_edit_password_manager

import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.core.BaseFragment
import com.burujiyaseer.passwordmanager.databinding.FragmentAddOrEditPasswordBinding
import com.burujiyaseer.passwordmanager.ui.util.collectFlowLatest
import com.burujiyaseer.passwordmanager.ui.util.getCurrentText
import com.burujiyaseer.passwordmanager.ui.util.onTextAdded
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrEditPasswordManagerFragment : BaseFragment<FragmentAddOrEditPasswordBinding>(
    FragmentAddOrEditPasswordBinding::inflate
) {

    private val viewModel by viewModels<AddOrEditPasswordManagerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // intercept back button to warn user about exiting.
        activity?.onBackPressedDispatcher?.addCallback(this) {
            viewModel.onEvent(AddEditPasswordManagerEvent.LeavePasswordEntryClicked)
        }
    }

    override fun FragmentAddOrEditPasswordBinding.setupViewListeners() {
        toolbar.setNavigationOnClickListener {
            viewModel.onEvent(AddEditPasswordManagerEvent.LeavePasswordEntryClicked)
        }
        etEntryTitle.onTextAdded { title ->
            viewModel.onEvent(AddEditPasswordManagerEvent.AddedTitle(title))
        }
        etPassword.onTextAdded { inputText ->
            viewModel.onEvent(AddEditPasswordManagerEvent.AddedPassword(inputText))
        }
        etWebsite.onTextAdded { inputText ->
            viewModel.onEvent(AddEditPasswordManagerEvent.AddedWebsite(inputText))
        }
        btnDone.setOnClickListener {
            viewModel.onEvent(
                AddEditPasswordManagerEvent.SavePasswordEntry(
                    UIPasswordModel(
                        null,
                        title = etEntryTitle.getCurrentText(),
                        account = etAccount.getCurrentText(),
                        username = etUsername.getCurrentText(),
                        password = etPassword.getCurrentText(),
                        websiteUrl = etWebsite.getCurrentText(),
                        description = etDescription.getCurrentText()
                    )
                )
            )
        }
    }

    override fun setupDataListeners() {
        collectFlowLatest(viewModel.shouldEnableDoneButton) {
            onAction(it)
        }
        collectFlowLatest(viewModel.uiActionFlow) { uiAction ->
            onAction(uiAction)
        }
        collectFlowLatest(viewModel.currentPasswordUIState) { fillUIFieldsAction ->
            onAction(fillUIFieldsAction)
        }
    }

    private fun onAction(uiAction: AddEditPasswordManagerAction?) {
        binding.progressBar.isVisible = uiAction is AddEditPasswordManagerAction.ShowLoader
        when (uiAction) {
            AddEditPasswordManagerAction.ShowConfirmDeleteDialog -> showConfirmDeleteDialog()
            AddEditPasswordManagerAction.ShowConfirmExitDialog -> showConfirmExitDialog()

            AddEditPasswordManagerAction.NavigateBack -> {
                transitionHelper.invokeSharedAxisTransitionBackward()
                findNavController().popBackStack()
            }

            is AddEditPasswordManagerAction.ToggleDoneBtnEnabled -> binding.btnDone.isEnabled =
                uiAction.isEnabled

            is AddEditPasswordManagerAction.FillUIFields -> binding.apply {
                toolbar.title = getString(uiAction.titleResId)
                uiAction.uiPasswordModel?.apply {
                    etEntryTitle.setText(title)
                    etAccount.setText(account)
                    etUsername.setText(username)
                    etPassword.setText(password)
                    etWebsite.setText(websiteUrl)
                    etDescription.setText(description)
                }
            }

            AddEditPasswordManagerAction.ShowLoader, null -> Unit
        }
    }

    private fun showConfirmDeleteDialog() {
        buildDialog().setMessage(R.string.delete_alert)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                viewModel.onEvent(AddEditPasswordManagerEvent.PositiveDeletePasswordEntry)
                dialog.dismiss()
            }
            .show()
    }

    private fun showConfirmExitDialog() {
        buildDialog().setMessage(R.string.leave_alert)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                viewModel.onEvent(AddEditPasswordManagerEvent.PositiveLeavePasswordEntry)
                dialog.dismiss()
            }
            .show()
    }

    private fun buildDialog() = MaterialAlertDialogBuilder(requireContext()).apply {
        setTitle(R.string.warning_dialog_title)
        setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
    }
}