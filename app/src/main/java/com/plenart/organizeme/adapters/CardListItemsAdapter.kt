package com.plenart.organizeme.adapters

import android.content.Context
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
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.CardDiffCallback
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.visible

class CardListItemsAdapter(
    private val context: Context,
    private val members: ArrayList<User>,
    private val taskListCallback: ITaskListCallback,
    private val taskPosition: Int
) :
    ListAdapter<Card, CardListItemsAdapter.CardItemViewHolder>(CardDiffCallback()) {

    private var onClickListener: CardItemClickInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, position)
    }

    fun setOnClickListener(onClickInterface: CardItemClickInterface) {
        this.onClickListener = onClickInterface
    }

    inner class CardItemViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card, position: Int) {
            bindColor(card)
            binding.tvCardName.text = card.name
            setupSelectedMembers(card, position)

            binding.itemCard.setOnClickListener {
                taskListCallback.cardDetails(taskPosition,absoluteAdapterPosition)
            }
        }

        private fun bindColor(card: Card) {
            if (card.labelColor.isNotEmpty()) {
                binding.apply {
                    binding.viewLabelColor.visible()
                    binding.viewLabelColor.setBackgroundColor(Color.parseColor(card.labelColor))
                }
            } else
                binding.viewLabelColor.gone()

        }

        private fun setupSelectedMembers(card: Card, position: Int) {
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
                        val adapterCardMembers =
                            CardMembersListItemAdapter(context, selectedMembersList, false)

                        binding.apply {
                            rvCardSelectedMembersList.apply {
                                visible()
                                layoutManager = GridLayoutManager(context, 4)
                                adapter = adapterCardMembers
                            }
                        }

                        adapterCardMembers.setOnClickListener(object : MemberItemClickInterface {
                            override fun onClick() {
                                onClickListener?.onClick(position)

                            }
                        })

                    }
                } else {
                    binding.rvCardSelectedMembersList.gone()
                }

            }
        }

    }


}