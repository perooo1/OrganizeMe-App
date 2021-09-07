package com.plenart.organizeme.utils

import androidx.recyclerview.widget.DiffUtil
import com.plenart.organizeme.models.Task

class TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) =
        oldItem.title == newItem.title


    override fun areContentsTheSame(oldItem: Task, newItem: Task) =
        oldItem == newItem

}