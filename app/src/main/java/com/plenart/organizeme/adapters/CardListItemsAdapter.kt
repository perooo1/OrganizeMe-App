package com.plenart.organizeme.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemCardBinding
import com.plenart.organizeme.interfaces.CardItemClickInterface
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.SelectedMembers
import com.plenart.organizeme.utils.CardDiffCallback
import com.plenart.organizeme.viewModels.TaskListViewModel

class CardListItemsAdapter(
    private val context: Context,
    private val viewModel: TaskListViewModel
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
        }

        private fun bindColor(card: Card) {
            if (card.labelColor.isNotEmpty()) {
                binding.apply {
                    binding.viewLabelColor.visibility = View.VISIBLE
                    binding.viewLabelColor.setBackgroundColor(Color.parseColor(card.labelColor))
                }
            } else
                binding.viewLabelColor.visibility = View.GONE

        }

        private fun setupSelectedMembers(card: Card, position: Int) {
            if (viewModel.assignedMemberDetailList.value?.size!! > 0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList();

                for (i in viewModel.assignedMemberDetailList.value?.indices!!) {
                    for (j in card.assignedTo) {
                        if (viewModel.assignedMemberDetailList.value!![i].id == j) {

                            val selectedMembers = SelectedMembers(
                                viewModel.assignedMemberDetailList.value!![i].id,
                                viewModel.assignedMemberDetailList.value!![i].image
                            )

                            selectedMembersList.add(selectedMembers)

                        }
                    }
                }

                if (selectedMembersList.size > 0) {
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == card.createdBy) {
                        binding.rvCardSelectedMembersList.visibility = View.GONE
                    } else {
                        binding.rvCardSelectedMembersList.visibility = View.VISIBLE
                        binding.rvCardSelectedMembersList.layoutManager =
                            GridLayoutManager(context, 4)

                        val adapter =
                            CardMembersListItemAdapter(context, selectedMembersList, false)
                        binding.rvCardSelectedMembersList.adapter = adapter

                        adapter.setOnClickListener(object : MemberItemClickInterface {
                            override fun onClick() {

                                if (onClickListener != null) {
                                    onClickListener!!.onClick(position)
                                }

                            }

                        })


                    }
                } else {
                    binding.rvCardSelectedMembersList.visibility = View.GONE
                }

            }
        }

    }


}