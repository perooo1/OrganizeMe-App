package com.plenart.organizeme.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemTaskBinding
import com.plenart.organizeme.models.Task

class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)                            //CONTEXT
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp().toPx()),0);
        view.layoutParams = layoutParams;
        val binding = ItemTaskBinding.bind(view);                                           //CAREFUL, POTENTIAL PROBLEM!!
        return ListItemViewHolder(binding);
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position];
        if(holder is ListItemViewHolder){
            if(position == list.size-1){
                holder.binding.tvAddTaskList.visibility = View.VISIBLE;
                holder.binding.llTaskItem.visibility = View.GONE;
            }
            else{
                holder.binding.tvAddTaskList.visibility = View.GONE;
                holder.binding.llTaskItem.visibility = View.VISIBLE;

            }
        }

    }

    override fun getItemCount(): Int {
        return list.size;
    }

    private fun Int.toDp(): Int = (this/Resources.getSystem().displayMetrics.density).toInt();
    private fun Int.toPx(): Int = (this*Resources.getSystem().displayMetrics.density).toInt();

    inner class ListItemViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){

    }

}