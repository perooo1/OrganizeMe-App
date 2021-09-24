package com.plenart.organizeme.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ItemTaskBinding
import com.plenart.organizeme.interfaces.CardItemClickInterface
import com.plenart.organizeme.interfaces.ITaskListCallback
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.TaskDiffCallback
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.visible
import java.util.*

class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>,
    private val taskListCallback: ITaskListCallback,
    private val members: ArrayList<User>
) :
    ListAdapter<Task, TaskListItemsAdapter.ListItemViewHolder>(TaskDiffCallback()) {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp().toPx()), 0)
        view.layoutParams = layoutParams
        val binding = ItemTaskBinding.bind(view)
        return ListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, position)

    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)

        builder.apply {
            setTitle("Alert");
            setMessage("Are you sure you want to delete $title?")
            setIcon(android.R.drawable.ic_dialog_alert);

            setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss();
                taskListCallback.deleteTaskList(position)
            }

            setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    inner class ListItemViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task, position: Int) {
            if (position == list.size - 1) {
                binding.apply {
                    tvAddTaskList.visible()
                    llTaskItem.gone()
                }
            } else {
                binding.apply {
                    tvAddTaskList.gone()
                    llTaskItem.visible()
                }
            }

            binding.apply {
                tvTaskListTitle.text = task.title
                tvAddTaskList.setOnClickListener {
                    tvAddTaskList.gone()
                    cvAddTaskListName.visible()
                }

                ibCloseListName.setOnClickListener {
                    tvAddTaskList.visible()
                    cvAddTaskListName.gone()
                }

            }

            addList()
            editList(task, position)
            deleteList(task, position)
            displayCards(task, position)

        }

        private fun addList() {
            binding.apply {
                ibDoneListName.setOnClickListener {
                    val listName = etTaskListName.text.toString()
                    if (listName.isNotEmpty()) {
                        taskListCallback.createTaskList(listName)

                    } else {
                        Toast.makeText(context, "Please enter list name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun editList(task: Task, position: Int) {
            binding.apply {
                ibEditListName.setOnClickListener {
                    etEditTaskListName.setText(task.title)
                    llTitleView.gone()
                    cvEditTaskListName.visible()
                }

                ibCloseEditableView.setOnClickListener {
                    llTitleView.visible()
                    cvEditTaskListName.gone()
                }

                ibDoneEditListName.setOnClickListener {
                    val listName = etEditTaskListName.text.toString();

                    if (listName.isNotEmpty()) {
                        taskListCallback.updateTaskList(position, listName, task)
                    } else {
                        Toast.makeText(context, "Please enter list name", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

            }
        }

        private fun deleteList(task: Task, position: Int) {
            binding.ibDeleteListName.setOnClickListener {
                alertDialogForDeleteList(position, task.title)
            }
        }

        private fun displayCards(task: Task, position: Int) {
            binding.apply {
                tvAddCard.setOnClickListener {
                    tvAddCard.gone()
                    cvAddCard.visible()
                }

                ibCloseCardName.setOnClickListener {
                    tvAddCard.visible()
                    cvAddCard.gone()
                }

                ibDoneCardName.setOnClickListener {
                    val cardName = binding.etCardName.text.toString();
                    if (cardName.isNotEmpty()) {
                        taskListCallback.addCardToTaskList(position, cardName)
                    } else {
                        Toast.makeText(context, "Please enter a card name!", Toast.LENGTH_SHORT)
                            .show();
                    }

                }

            }

            loadCards(task, position)

            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            binding.rvCardList.addItemDecoration(dividerItemDecoration)

            repositionCards(task, position)

        }

        private fun loadCards(task: Task, position: Int) {
            val adapterCard = CardListItemsAdapter(context, members)
            adapterCard.submitList(task.cards)

            binding.apply {
                rvCardList.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = adapterCard
                }

            }

            adapterCard.setOnClickListener(object : CardItemClickInterface {
                override fun onClick(cardPosition: Int) {
                    Log.i("loadCards","inside override")

                    taskListCallback.cardDetails(
                        position,
                        cardPosition
                    )         //first position is taskList position
                }

            })

        }

        private fun repositionCards(task: Task, position: Int) {
            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = viewHolder.absoluteAdapterPosition
                    val targetPosition = target.absoluteAdapterPosition

                    if (mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = draggedPosition
                    }
                    mPositionDraggedTo = targetPosition
                    Collections.swap(list[position].cards, draggedPosition, targetPosition)

                    bindingAdapter?.notifyItemMoved(
                        draggedPosition,
                        targetPosition
                    )             //careful!
                    //adapter.notifyItemMoved(draggedPosition,targetPosition)
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
                    if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {
                        taskListCallback.updateCardsInTaskList(position, task.cards)
                    }

                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }

            }
            )

            helper.attachToRecyclerView(binding.rvCardList)
        }

    }
}
