package com.plenart.organizeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemMemberBinding
import com.plenart.organizeme.interfaces.SelectedMembersClickInterface
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants

class MemberListItemAdapter(private val context: Context, private val list: ArrayList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: SelectedMembersClickInterface? = null;

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

            if(model.selected){
                holder.binding.ivSelectedMembers.visibility = View.VISIBLE;
            }
            else{
                holder.binding.ivSelectedMembers.visibility = View.GONE;
            }

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    if(model.selected){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT);
                    }
                    else{
                        onClickListener!!.onClick(position,model,Constants.SELECT);
                    }
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    fun setOnClickListener(onClickListener: SelectedMembersClickInterface){
        this.onClickListener = onClickListener;
    }

    inner class MemberItemViewHolder(val binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root){

    }

}