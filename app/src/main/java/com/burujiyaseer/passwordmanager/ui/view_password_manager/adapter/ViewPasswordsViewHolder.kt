package com.burujiyaseer.passwordmanager.ui.view_password_manager.adapter

import android.graphics.BitmapFactory
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.burujiyaseer.passwordmanager.R
import com.burujiyaseer.passwordmanager.databinding.ItemPasswordManagerEntryBinding
import com.burujiyaseer.passwordmanager.domain.model.PasswordManagerModel

class ViewPasswordsViewHolder(
    private val binding: ItemPasswordManagerEntryBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    interface Listener {
        fun onEditClickedListener(entryId: String)
    }

    fun bind(passwordManagerModel: PasswordManagerModel) {
        setupTitle(passwordManagerModel.title)
        setupAccount(passwordManagerModel.account)
        setupImage(passwordManagerModel)
        binding.btnEdit.setOnClickListener {
            listener.onEditClickedListener(passwordManagerModel.entryId)
        }
    }

    fun setupTitle(title: String) {
        binding.tvEntryTitle.text = title
    }

    fun setupAccount(account: String) {
        binding.tvEntryAccount.text = account
    }

    fun setupImage(passwordManagerModel: PasswordManagerModel) = binding.apply {
        val favIconUrl = passwordManagerModel.favIconUrl
        if (favIconUrl != null) {
            ivFavIcon.isVisible = true
            tvInitials.isVisible = false
            ivFavIcon.setImageBitmap(
                BitmapFactory.decodeFile(favIconUrl)
            )
        } else tvInitials.apply {
            ivFavIcon.isVisible = false
            isVisible = true
            text = passwordManagerModel.title.getOrNull(0)?.toString()
            val colorArray = itemView.resources.obtainTypedArray(R.array.color_array)
            // randomize the background tint of the text view
            backgroundTintList = itemView.context.getColorStateList(
                colorArray.getResourceId(
                    passwordManagerModel.createdAt.toInt() % colorArray.length(),
                    R.color.warning_color
                )
            )
            colorArray.recycle()
        }
    }
}