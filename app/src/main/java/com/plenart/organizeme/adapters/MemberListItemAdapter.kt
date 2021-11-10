package com.plenart.organizeme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemMemberBinding
import com.plenart.organizeme.interfaces.SelectedMembersClickInterface
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.loadImage
import com.plenart.organizeme.utils.visible

class MemberListItemAdapter(
    private val list: ArrayList<User>
) :
    RecyclerView.Adapter<MemberListItemAdapter.MemberItemViewHolder>() {

    private var onClickListener: SelectedMembersClickInterface? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberListItemAdapter.MemberItemViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberItemViewHolder(binding);

    }

    override fun onBindViewHolder(holder: MemberItemViewHolder, position: Int) {
        val model = list[position]

        holder.bind(model, position)

        holder.itemView.setOnClickListener {

            if (onClickListener != null) {
                if (model.selected) {
                    onClickListener?.onClick(position, model, Constants.UN_SELECT)
                } else {
                    onClickListener?.onClick(position, model, Constants.SELECT)
                }
            }


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun setOnClickListener(onClickListener: SelectedMembersClickInterface) {
        this.onClickListener = onClickListener
    }

    inner class MemberItemViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: User, position: Int) {

            binding.ivMemberImage.loadImage(model.image)

            binding.apply {
                tvMemberNameItemMember.text = model.name
                tvMemberEmailItemMember.text = model.email

                if (model.selected) {
                    ivSelectedMembers.visible()
                } else {
                    ivSelectedMembers.gone()
                }
            }

        }
    }


}