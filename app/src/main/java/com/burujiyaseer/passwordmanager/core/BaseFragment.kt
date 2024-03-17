package com.burujiyaseer.passwordmanager.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Base fragment to handle [ViewBinding] boiler plate code, transition animations etc.
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment() {

    private var _binding: VB? = null

    val binding: VB
        get() = _binding as VB

    protected val transitionHelper by lazy { TransitionHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transitionHelper.invokeTopLevelEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupViewListeners()
        setupDataListeners()
    }

    /**
     * set up view related listeners with [VB] as receiver for convenience
     * this method is called in [onViewCreated]
     */
    abstract fun VB.setupViewListeners()

    /**
     * set up data and viewModel related listeners
     * this method is called in [onViewCreated]
     */
    abstract fun setupDataListeners()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}