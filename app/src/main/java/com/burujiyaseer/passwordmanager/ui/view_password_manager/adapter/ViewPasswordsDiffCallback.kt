package com.burujiyaseer.passwordmanager.ui.view_password_manager.adapter

import androidx.recyclerview.widget.DiffUtil
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

class ViewPasswordsDiffCallback : DiffUtil.ItemCallback<PasswordManagerModel>() {
    override fun areItemsTheSame(
        oldItem: PasswordManagerModel,
        newItem: PasswordManagerModel
    ): Boolean = oldItem.entryId == newItem.entryId

    override fun areContentsTheSame(
        oldItem: PasswordManagerModel,
        newItem: PasswordManagerModel
    ): Boolean = oldItem == newItem

    override fun getChangePayload(
        oldItem: PasswordManagerModel,
        newItem: PasswordManagerModel
    ): Any? {
        return if (oldItem.favIconUrl != newItem.favIconUrl) Payload.IMAGE
        else if (oldItem.title != newItem.title) Payload.TITLE
        else if (oldItem.account != newItem.account) Payload.ACCOUNT
        else super.getChangePayload(oldItem, newItem)
    }
}

enum class Payload {
    TITLE,
    ACCOUNT,
    IMAGE
}