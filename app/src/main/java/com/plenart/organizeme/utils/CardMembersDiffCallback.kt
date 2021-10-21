package com.plenart.organizeme.utils

import androidx.recyclerview.widget.DiffUtil
import com.plenart.organizeme.models.SelectedMembers

class CardMembersDiffCallback: DiffUtil.ItemCallback<SelectedMembers>() {
    override fun areItemsTheSame(oldItem: SelectedMembers, newItem: SelectedMembers) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SelectedMembers, newItem: SelectedMembers) =
        oldItem == newItem
}