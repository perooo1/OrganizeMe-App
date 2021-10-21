package com.plenart.organizeme.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemLabelColorBinding
import com.plenart.organizeme.interfaces.LabelColorClickedInterface

class LabelColorListAdapter(
    private val list: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<LabelColorListAdapter.ItemLabelColorViewHolder>() {

    var onItemClickListener: LabelColorClickedInterface? = null;

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LabelColorListAdapter.ItemLabelColorViewHolder {
        val binding =
            ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemLabelColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemLabelColorViewHolder, position: Int) {
        val item = list[position]

        holder.binding.viewMain.setBackgroundColor(Color.parseColor(item))

        if (item == mSelectedColor) {
            holder.binding.ivSelectedColor.visibility = View.VISIBLE
        } else {
            holder.binding.ivSelectedColor.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener!!.onClick(position, item)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickInterface: LabelColorClickedInterface) {
        this.onItemClickListener = onClickInterface
    }

    inner class ItemLabelColorViewHolder(val binding: ItemLabelColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}