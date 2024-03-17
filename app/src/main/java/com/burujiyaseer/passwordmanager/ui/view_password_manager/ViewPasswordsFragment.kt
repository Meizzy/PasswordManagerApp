package com.burujiyaseer.passwordmanager.ui.view_password_manager

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.core.BaseFragment
import com.burujiyaseer.passwordmanager.databinding.FragmentViewPasswordManagerBinding
import com.burujiyaseer.passwordmanager.ui.util.collectFlowLatest
import com.burujiyaseer.passwordmanager.ui.util.utilLog
import com.burujiyaseer.passwordmanager.ui.view_password_manager.adapter.ViewPasswordsAdapter
import com.burujiyaseer.passwordmanager.ui.view_password_manager.adapter.ViewPasswordsViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPasswordsFragment : BaseFragment<FragmentViewPasswordManagerBinding>(
    FragmentViewPasswordManagerBinding::inflate
) {
    private val viewModel by viewModels<ViewPasswordsViewModel>()

    private lateinit var viewPasswordsAdapter: ViewPasswordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        viewPasswordsAdapter = ViewPasswordsAdapter(
            object : ViewPasswordsViewHolder.Listener {
                override fun onEditClickedListener(entryId: String) {
                    viewModel.onPasswordEntryClicked(entryId)
                }
            }
        )
    }

    override fun FragmentViewPasswordManagerBinding.setupViewListeners() {
        fabAddPassword.setOnClickListener {
            viewModel.onAddPasswordClicked()
        }
        val searchView = toolbar.menu.findItem(R.id.itemSearch).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.onQueryTextChanged(newText)
                }
                return false
            }
        })
        rvPasswordManager.apply {
            itemAnimator = null
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = viewPasswordsAdapter
        }
    }

    override fun setupDataListeners() {
        collectFlowLatest(viewModel.uiActionFlow) { uiAction ->
            onAction(uiAction)
        }
        collectFlowLatest(viewModel.navigationActionState) { navigationAction ->
            onAction(navigationAction)
        }
        collectFlowLatest(viewModel.passwordEntriesUIState) { passwordManagerState ->
            onAction(passwordManagerState)
        }
    }

    private fun onAction(uiAction: ViewPasswordUIAction?) {
        utilLog("viewPasswordsFragment, action: $uiAction")
        when (uiAction) {
            is ViewPasswordUIAction.NavigationAction -> {
                viewModel.onNavigationHandled()
                transitionHelper.invokeSharedAxisTransitionForward()
                findNavController().navigate(uiAction.destinationId, uiAction.args)
            }

            null -> Unit
            ViewPasswordUIAction.ShowEmptyListPrompt -> showEmptyListPrompt(R.string.empty_password_entries)
            ViewPasswordUIAction.ShowEmptySearchResults -> showEmptyListPrompt(R.string.no_search_results)
            is ViewPasswordUIAction.SubmitData -> {
                binding.rvPasswordManager.isVisible = true
                viewPasswordsAdapter.submitList(uiAction.filteredQueries)
                binding.tvEmptyResults.isVisible = false
            }
        }
    }

    private fun showEmptyListPrompt(@StringRes messageRes: Int) {
        binding.tvEmptyResults.apply {
            isVisible = true
            binding.rvPasswordManager.isVisible = false
            setText(messageRes)
        }
    }
}