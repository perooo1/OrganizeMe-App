package com.plenart.organizeme.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemCardBinding
import com.plenart.organizeme.interfaces.CardItemClickInterface
import com.plenart.organizeme.interfaces.ITaskListCallback
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.SelectedMembers
import com.plenart.organizeme.utils.CardDiffCallback
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.visible

class CardListItemsAdapter(
    private val taskListCallback: ITaskListCallback,
    private val taskPosition: Int,
    private val cardItemClickListener: CardItemClickInterface
) :
    ListAdapter<Card, CardListItemsAdapter.CardItemViewHolder>(CardDiffCallback()) {

    private lateinit var cardMembersListItemAdapter: CardMembersListItemAdapter
    private val members = taskListCallback.getAssignedMembersDetailList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }

    inner class CardItemViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            setLabelColor(card)
            binding.tvCardName.text = card.name
            setupSelectedMembers(card)

            binding.itemCard.setOnClickListener {
                taskListCallback.cardDetails(taskPosition, absoluteAdapterPosition)
            }
        }

        private fun setLabelColor(card: Card) {
            if (card.labelColor.isNotEmpty()) {
                binding.viewLabelColor.apply {
                    visible()
                    setBackgroundColor(Color.parseColor(card.labelColor))
                }
            } else {
                binding.viewLabelColor.gone()
            }

        }

        private fun setupSelectedMembers(card: Card) {
            if (members.size > 0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
                for (i in members.indices) {
                    for (j in card.assignedTo) {
                        if (members[i].id == j) {
                            val selectedMembers = SelectedMembers(
                                members[i].id,
                                members[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }

                if (selectedMembersList.size > 0) {
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == card.createdBy) {
                        binding.rvCardSelectedMembersList.gone()
                    } else {
                        setupCardMembersRecycler(selectedMembersList)
                    }
                } else {
                    binding.rvCardSelectedMembersList.gone()
                }

            }
        }

        private fun setupCardMembersRecycler(selectedMembersList: ArrayList<SelectedMembers>) {

            val listener = object : MemberItemClickInterface{
                override fun onClick() {
                    cardItemClickListener.onClick(bindingAdapterPosition)
                }
            }

            cardMembersListItemAdapter = CardMembersListItemAdapter(false,listener)
            cardMembersListItemAdapter.submitList(selectedMembersList)      //don't know if it makes sense bc it's not in observer

            binding.rvCardSelectedMembersList.apply {
                visible()
                layoutManager = GridLayoutManager(context, 4)
                adapter = cardMembersListItemAdapter
            }

        }

    }


}