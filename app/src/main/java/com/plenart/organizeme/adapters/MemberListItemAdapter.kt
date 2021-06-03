package com.plenart.organizeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemMemberBinding
import com.plenart.organizeme.models.User

class MemberListItemAdapter(private val context: Context, private val list: ArrayList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false);
        return MemberItemViewHolder(binding);

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];

        if(holder is MemberItemViewHolder){
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.binding.ivMemberImage);

            holder.binding.tvMemberNameItemMember.text = model.name;
            holder.binding.tvMemberEmailItemMember.text = model.email;

        }

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    inner class MemberItemViewHolder(val binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root){

    }

}