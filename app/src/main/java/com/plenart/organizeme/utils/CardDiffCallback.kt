package com.plenart.organizeme.utils

import androidx.recyclerview.widget.DiffUtil
import com.plenart.organizeme.models.Card

class CardDiffCallback: DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card) =
        oldItem.name == newItem.name


    override fun areContentsTheSame(oldItem: Card, newItem: Card) =
        oldItem == newItem

}