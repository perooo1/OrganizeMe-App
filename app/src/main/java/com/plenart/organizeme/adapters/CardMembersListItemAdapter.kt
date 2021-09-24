package com.plenart.organizeme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemCardSelectedMemberBinding
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.SelectedMembers
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.visible

class CardMembersListItemAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: MemberItemClickInterface? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        );
        return CardMemberItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];

        if (holder is CardMemberItemViewHolder) {
            holder.bind(model,position)

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick();
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    fun setOnClickListener(onClickInterface: MemberItemClickInterface) {
        this.onClickListener = onClickInterface;
    }


    inner class CardMemberItemViewHolder(val binding: ItemCardSelectedMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SelectedMembers, position: Int) {
            if (position == list.size - 1 && assignMembers) {
                binding.apply {
                    ivAddMember.visible()
                    ivSelectedMemberImage.gone()

                }
            } else {
                binding.apply {
                    ivAddMember.gone()
                    ivSelectedMemberImage.visible()
                }
            }

            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivSelectedMemberImage)

        }
    }

}



