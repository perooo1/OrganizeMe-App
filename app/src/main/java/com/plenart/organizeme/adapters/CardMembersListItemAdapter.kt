package com.plenart.organizeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemCardSelectedMemberBinding
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.SelectedMembers

class CardMembersListItemAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: MemberItemClickInterface? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false);
        return CardMemberItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];

        if(holder is CardMemberItemViewHolder){
            if(position == list.size -1 && assignMembers){
                holder.binding.ivAddMember.visibility = View.VISIBLE;
                holder.binding.ivSelectedMemberImage.visibility = View.GONE;
            }
            else{
                holder.binding.ivAddMember.visibility = View.GONE;
                holder.binding.ivSelectedMemberImage.visibility = View.VISIBLE;

                Glide.with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.binding.ivSelectedMemberImage);

            }

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick();
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    fun setOnClickListener(onClickInterface: MemberItemClickInterface){
        this.onClickListener = onClickInterface;
    }


    inner class CardMemberItemViewHolder(val binding: ItemCardSelectedMemberBinding ): RecyclerView.ViewHolder(binding.root){

    }


}