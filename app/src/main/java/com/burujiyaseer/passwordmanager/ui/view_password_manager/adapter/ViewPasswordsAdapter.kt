package com.burujiyaseer.passwordmanager.ui.view_password_manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.burujiyaseer.passwordmanager.databinding.ItemPasswordManagerEntryBinding
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

class ViewPasswordsAdapter(
    private val listener: ViewPasswordsViewHolder.Listener
) : ListAdapter<PasswordManagerModel, ViewPasswordsViewHolder>(ViewPasswordsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPasswordsViewHolder =
        ViewPasswordsViewHolder(
            binding = ItemPasswordManagerEntryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            listener
        )

    override fun onBindViewHolder(holder: ViewPasswordsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ViewPasswordsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val currentItem = getItem(position)
        when (payloads.getOrNull(position)) {
            Payload.TITLE -> holder.setupTitle(currentItem.title)
            Payload.ACCOUNT -> holder.setupAccount(currentItem.account)
            Payload.IMAGE -> holder.setupImage(currentItem)
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }
}