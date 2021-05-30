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

    private var boardItemClickListener: BoardItemClickInterface? = null;


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false);

        return MyViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];

        if(holder is MyViewHolder){
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.binding.ivBoardImageItemBoard);

            holder.binding.tvNameItemBoard.text = model.name;
            holder.binding.tvCreatedByItemBoard.text = "Created by: ${model.createdBy}";

            holder.itemView.setOnClickListener {
                if(boardItemClickListener != null){
                    boardItemClickListener!!.onClick(position, model);
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size;

    }

    inner class MyViewHolder(val binding:ItemBoardBinding):RecyclerView.ViewHolder(binding.root){
    }

}