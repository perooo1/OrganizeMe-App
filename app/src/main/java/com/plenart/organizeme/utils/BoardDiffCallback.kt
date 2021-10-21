package com.plenart.organizeme.utils

import androidx.recyclerview.widget.DiffUtil
import com.plenart.organizeme.models.Board

class BoardDiffCallback : DiffUtil.ItemCallback<Board>() {
    override fun areItemsTheSame(oldItem: Board, newItem: Board) =
        oldItem.documentID == newItem.documentID

    override fun areContentsTheSame(oldItem: Board, newItem: Board) =
        oldItem == newItem
}