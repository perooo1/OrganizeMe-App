package com.plenart.organizeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemCardBinding
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.interfaces.CardItemClickInterface
import com.plenart.organizeme.models.Card

class CardListItemsAdapter(private val context: Context, private var list: ArrayList<Card>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: CardItemClickInterface? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false);
        return CardItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];

        if(holder is CardItemViewHolder){
            holder.binding.tvCardName.text = model.name;

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position);
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    fun setOnClickListener(onClickInterface: CardItemClickInterface){
        this.onClickListener = onClickInterface;
    }

    inner class CardItemViewHolder(val binding: ItemCardBinding ): RecyclerView.ViewHolder(binding.root){

    }

}