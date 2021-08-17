package com.plenart.organizeme.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.databinding.ItemCardBinding
import com.plenart.organizeme.fragments.TaskListFragment
import com.plenart.organizeme.interfaces.CardItemClickInterface
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.SelectedMembers

class CardListItemsAdapter(private val context: Context, private var list: ArrayList<Card>, private val fragment: TaskListFragment ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: CardItemClickInterface? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false);
        return CardItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];

        if(holder is CardItemViewHolder){
            
            if(model.labelColor.isNotEmpty()){
                holder.binding.viewLabelColor.visibility = View.VISIBLE;
                holder.binding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor));
            }
            else{
                holder.binding.viewLabelColor.visibility = View.GONE;
            }
            
            holder.binding.tvCardName.text = model.name;

            if(fragment.viewModel.assignedMemberDetailList.value?.size!! > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList();

                for(i in fragment.viewModel.assignedMemberDetailList.value?.indices!!){
                    for(j in model.assignedTo){
                        if(fragment.viewModel.assignedMemberDetailList.value!![i].id == j){

                            val selectedMembers = SelectedMembers(
                                fragment.viewModel.assignedMemberDetailList.value!![i].id,
                                fragment.viewModel.assignedMemberDetailList.value!![i].image
                            );

                            selectedMembersList.add(selectedMembers);

                        }
                    }

                }

            /*                                                                                              JUST A BACKUP
            if((context as TaskListActivity).viewModel.assignedMemberDetailList.value?.size!! > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList();

                for(i in context.viewModel.assignedMemberDetailList.value?.indices!!){
                    for(j in model.assignedTo){
                        if(context.viewModel.assignedMemberDetailList.value!![i].id == j){

                            val selectedMembers = SelectedMembers(
                                context.viewModel.assignedMemberDetailList.value!![i].id,
                                context.viewModel.assignedMemberDetailList.value!![i].image
                            );

                            selectedMembersList.add(selectedMembers);

                        }
                    }

            }

             */


                if(selectedMembersList.size > 0){
                    if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        holder.binding.rvCardSelectedMembersList.visibility = View.GONE;
                    }
                    else{
                        holder.binding.rvCardSelectedMembersList.visibility = View.VISIBLE;
                        holder.binding.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context,4);

                        val adapter = CardMembersListItemAdapter(context, selectedMembersList,false);
                        holder.binding.rvCardSelectedMembersList.adapter = adapter;

                        adapter.setOnClickListener(object: MemberItemClickInterface{
                            override fun onClick() {
                                if(onClickListener != null){
                                    onClickListener!!.onClick(position);
                                }
                            }

                        })

                    }
                }

                else{
                    holder.binding.rvCardSelectedMembersList.visibility = View.GONE;
                }

            }

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position);
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    fun setOnClickListener(onClickInterface: CardItemClickInterface){
        this.onClickListener = onClickInterface;
    }

    inner class CardItemViewHolder(val binding: ItemCardBinding ): RecyclerView.ViewHolder(binding.root){

    }

}