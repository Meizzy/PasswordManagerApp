package com.burujiyaseer.passwordmanager.ui.util

import android.util.Log
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.burujiyaseer.passwordmanager.ui.util.Constants.EMPTY_STRING
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * extension function to [collectLatest] flow with [repeatOnLifecycle] API,
 * default [lifecycleState] is [Lifecycle.State.STARTED]
 */
internal fun <V> Fragment.collectFlowLatest(
    collectableFlow: Flow<V>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    actionOnCollect: suspend (value: V) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            collectableFlow.collectLatest { value ->
                actionOnCollect(value)
            }
        }
    }
}

/**
 * extension function to [collectLatest] without [repeatOnLifecycle] API
 */
internal fun <V> Fragment.collectFlowLatestOnce(
    collectableFlow: Flow<V>,
    actionOnCollect: suspend (value: V) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        collectableFlow.collectLatest { value ->
            actionOnCollect(value)
        }
    }
}

/**
 * extension function to [Flow.collect] flow with [repeatOnLifecycle] API,
 * default [lifecycleState] is [Lifecycle.State.STARTED]
 */
internal fun <V> Fragment.collectFlow(
    collectableFlow: Flow<V>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    actionOnCollect: suspend (value: V) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) {
            collectableFlow.collect { value ->
                actionOnCollect(value)
            }
        }
    }
}


internal fun utilLog(message: Any?) {
    Log.d("PasswordManagerTAG", message.toString())
}

/**
 * subscribe to [doOnTextChanged] and get the text in a [String] if not null
 */
internal inline fun TextInputEditText.onTextAdded(crossinline viewModelEvent: (inputText: String) -> Unit) =
    doOnTextChanged { text, _, _, _ ->
        text?.toString()?.let {
            viewModelEvent.invoke(it)
        }
    }

/**
 * @return [TextInputEditText.getText] trimmed, if null returns an empty string
 */
internal fun TextInputEditText.getCurrentText() = text?.trim()?.toString() ?: EMPTY_STRING

