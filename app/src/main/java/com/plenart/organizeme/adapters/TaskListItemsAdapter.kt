package com.plenart.organizeme.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plenart.organizeme.R
import com.plenart.organizeme.activities.TaskListActivity
import com.plenart.organizeme.databinding.ItemTaskBinding
import com.plenart.organizeme.fragments.TaskListFragment
import com.plenart.organizeme.interfaces.CardItemClickInterface
import com.plenart.organizeme.models.Task
import java.util.*
import kotlin.collections.ArrayList

class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>, private val fragment: TaskListFragment):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   private var mPositionDraggedFrom = -1;
   private var mPositionDraggedTo = -1;

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

            holder.binding.tvTaskListTitle.text = model.title;
            holder.binding.tvAddTaskList.setOnClickListener {

                holder.binding.tvAddTaskList.visibility = View.GONE;
                holder.binding.cvAddTaskListName.visibility = View.VISIBLE;
            }

            holder.binding.ibCloseListName.setOnClickListener {
                holder.binding.tvAddTaskList.visibility = View.VISIBLE;
                holder.binding.cvAddTaskListName.visibility = View.GONE;
            }

            //adding list
            holder.binding.ibDoneListName.setOnClickListener {
                val listName = holder.binding.etTaskListName.text.toString();
                if(listName.isNotEmpty()){
                    fragment.createTaskList(listName)
                    /*
                    if(context is TaskListActivity){
                        context.createTaskList(listName);
                    }
                    */
                }
                else{
                    //Toast.makeText(this, "Please enter list name",Toast.LENGTH_SHORT).show();
                }

            }
            //editing
            holder.binding.ibEditListName.setOnClickListener {
                holder.binding.etEditTaskListName.setText(model.title);
                holder.binding.llTitleView.visibility = View.GONE;
                holder.binding.cvEditTaskListName.visibility = View.VISIBLE;

            }
            holder.binding.ibCloseEditableView.setOnClickListener {
                holder.binding.llTitleView.visibility = View.VISIBLE;
                holder.binding.cvEditTaskListName.visibility = View.GONE;
            }

            holder.binding.ibDoneEditListName.setOnClickListener {
                val listName = holder.binding.etEditTaskListName.text.toString();

                if(listName.isNotEmpty()){
                    fragment.updateTaskList(position,listName, model)
                    /*
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model);
                    }
                    */
                }
                else{
                    //Toast.makeText(fragment, "Please enter list name",Toast.LENGTH_SHORT).show();
                }

            }
            //deleting
            holder.binding.ibDeleteListName.setOnClickListener {
                alertDialogForDeleteList(position,model.title);
            }

            //showing cards UI
            holder.binding.tvAddCard.setOnClickListener {
                holder.binding.tvAddCard.visibility = View.GONE;
                holder.binding.cvAddCard.visibility = View.VISIBLE;
            }

            holder.binding.ibCloseCardName.setOnClickListener {
                holder.binding.tvAddCard.visibility = View.VISIBLE;
                holder.binding.cvAddCard.visibility = View.GONE;
            }
            holder.binding.ibDoneCardName.setOnClickListener {
                val cardName = holder.binding.etCardName.text.toString();

                if(cardName.isNotEmpty()){
                    fragment.addCardToTaskList(position,cardName)
                    /*
                    if(context is TaskListActivity){
                       context.addCardToTaskList(position, cardName);
                    }
                    */

                }
                else{
                    //Toast.makeText(fragment, "Please enter a card name!",Toast.LENGTH_SHORT).show();
                }

            }

            //loading cards into UI
            holder.binding.rvCardList.layoutManager = LinearLayoutManager(context);
            holder.binding.rvCardList.setHasFixedSize(true);

            val adapter = CardListItemsAdapter(context, model.cards, fragment);
            holder.binding.rvCardList.adapter = adapter;

            adapter.setOnClickListener(object : CardItemClickInterface{
                override fun onClick(cardPosition: Int) {
                    if(context is TaskListActivity){
                        context.cardDetails(position, cardPosition)
                    }
                }

            })

            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            holder.binding.rvCardList.addItemDecoration(dividerItemDecoration);

            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = viewHolder.absoluteAdapterPosition;               //careful!
                    val targetPosition = target.absoluteAdapterPosition;

                    if(mPositionDraggedFrom == -1){
                        mPositionDraggedFrom = draggedPosition;
                    }
                    mPositionDraggedTo = targetPosition;
                    Collections.swap(list[position].cards, draggedPosition, targetPosition);

                    adapter.notifyItemMoved(draggedPosition,targetPosition);
                    return false;

                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    TODO("Not yet implemented")
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    if(mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo){
                        (context as TaskListActivity).updateCardsInTaskList(position, list[position].cards)
                    }

                    mPositionDraggedFrom = -1;
                    mPositionDraggedTo = -1;
                }

            }
            )

            helper.attachToRecyclerView(holder.binding.rvCardList);

        }

    }


    override fun getItemCount(): Int {
        return list.size;
    }

    private fun Int.toDp(): Int = (this/Resources.getSystem().displayMetrics.density).toInt();
    private fun Int.toPx(): Int = (this*Resources.getSystem().displayMetrics.density).toInt();

    private fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure you want to delete $title?")
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("Yes"){dialogInterface, which ->
            dialogInterface.dismiss();
            if(context is TaskListActivity){
                context.deleteTaskList(position);
            }

        }

        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss();
        }

        val alertDialog: AlertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    inner class ListItemViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root){

    }

}