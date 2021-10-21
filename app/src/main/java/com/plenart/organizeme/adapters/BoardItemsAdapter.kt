package com.plenart.organizeme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemBoardBinding
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.BoardDiffCallback
import com.plenart.organizeme.utils.loadImage

class BoardItemsAdapter() :
    ListAdapter<Board, BoardItemsAdapter.BoardItemViewHolder>(BoardDiffCallback()) {

    private var boardItemClickListener: BoardItemClickInterface? = null

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
        holder.bindListeners(position, model)

    }

    fun setOnClickListener(onClickInterface: BoardItemClickInterface) {
        this.boardItemClickListener = onClickInterface
    }

    inner class BoardItemViewHolder(val binding: ItemBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindImage(image: String) {
            binding.ivBoardImageItemBoard.loadImage(image)

        }

        fun bindText(name: String, createdBy: String) {
            binding.apply {
                tvNameItemBoard.text = name
                tvCreatedByItemBoard.text = "Created by: $createdBy"
            }
        }

        fun bindListeners(position: Int, model: Board) {
            itemView.setOnClickListener {
                if (boardItemClickListener != null) {
                    boardItemClickListener?.onClick(position, model)
                }
            }
        }

    }

}