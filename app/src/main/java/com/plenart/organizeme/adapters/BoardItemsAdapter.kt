package com.plenart.organizeme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemBoardBinding
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.BoardDiffCallback
import com.plenart.organizeme.utils.loadImage

class BoardItemsAdapter(private val boardItemClickListener: (Board) -> Unit) :
    ListAdapter<Board, BoardItemsAdapter.BoardItemViewHolder>(BoardDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardItemsAdapter.BoardItemViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardItemViewHolder, position: Int) {
        val model = getItem(position)

        holder.bindImage(model.image)
        holder.bindText(model.name, model.createdBy)
        holder.bindListeners(model, boardItemClickListener)

    }

    inner class BoardItemViewHolder(val binding: ItemBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindImage(image: String) {
            binding.ivBoardImageItemBoard.loadImage(image)

        }

        fun bindText(name: String, createdBy: String) {
            binding.apply {
                tvNameItemBoard.text = name
                tvCreatedByItemBoard.text =
                    itemView.context.getString(R.string.board_created_by, createdBy)
            }
        }

        fun bindListeners(model: Board, boardItemClickListener: (Board) -> Unit) {
            binding.root.setOnClickListener { boardItemClickListener(model) }
        }

    }

}