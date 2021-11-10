package com.plenart.organizeme.interfaces

import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User

interface ITaskListCallback {
    fun createTaskList(taskListName: String)
    fun updateTaskList(position: Int, listName: String, model: Task)
    fun deleteTaskList(position: Int)
    fun addCardToTaskList(position: Int, cardName: String)
    fun cardDetails(taskListPosition: Int, cardPosition: Int)
    fun updateCardsInTaskList(position: Int, cards: ArrayList<Card>)
    fun getAssignedMembersDetailList(): ArrayList<User>
}