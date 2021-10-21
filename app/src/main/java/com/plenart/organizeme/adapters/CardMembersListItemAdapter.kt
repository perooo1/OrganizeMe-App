package com.plenart.organizeme.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemCardSelectedMemberBinding
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.SelectedMembers
import com.plenart.organizeme.utils.CardMembersDiffCallback
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.loadImage
import com.plenart.organizeme.utils.visible

class CardMembersListItemAdapter(
    private val assignMembers: Boolean
) : ListAdapter<SelectedMembers, CardMembersListItemAdapter.CardMemberItemViewHolder>(
    CardMembersDiffCallback()
) {

    private var onClickListener: MemberItemClickInterface? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardMembersListItemAdapter.CardMemberItemViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        );
        return CardMemberItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardMemberItemViewHolder, position: Int) {
        val model = getItem(position)

        holder.bind(model, position)
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick()
            }
        }

    }

    fun setOnClickListener(onClickInterface: MemberItemClickInterface) {
        this.onClickListener = onClickInterface;
    }

    inner class CardMemberItemViewHolder(val binding: ItemCardSelectedMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SelectedMembers, position: Int) {
            if (position == itemCount - 1 && assignMembers) {
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

            binding.ivSelectedMemberImage.loadImage(model.image)

        }
    }


}



