package com.plenart.organizeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemBoardBinding
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board

open class BoardItemsAdapter(private val context: Context, private val list: ArrayList<Board>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var boardItemClickListener: BoardItemClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BoardItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is BoardItemViewHolder){
            holder.bindImage(model.image)
            holder.bindText(model.name, model.createdBy)
            holder.bindListeners(position,model)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickInterface: BoardItemClickInterface){
        this.boardItemClickListener = onClickInterface
    }

    inner class BoardItemViewHolder(val binding:ItemBoardBinding):RecyclerView.ViewHolder(binding.root){

        fun bindImage(image: String){
            Glide.with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(binding.ivBoardImageItemBoard)
        }

        fun bindText(name: String, createdBy: String){
            binding.apply{
                tvNameItemBoard.text = name
                tvCreatedByItemBoard.text = "Created by: $createdBy"
            }
        }

        fun bindListeners(position: Int, model:Board){
            itemView.setOnClickListener{
                if(boardItemClickListener != null){
                    boardItemClickListener?.onClick(position,model)
                }
            }
        }

    }

}