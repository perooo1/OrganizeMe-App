package com.plenart.organizeme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User

class CardDetailsViewModel : ViewModel() {

    private val _assignedMemberDetailList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private var boardDetails: Board? = null
    private var taskListPosition: Int = -1
    private var cardPosition: Int = -1
    private var selectedColor: String = String()
    private var taskListUpdated: Boolean = false
    private var selectedDueDateMilis: Long = 0L
    private var cardName: String = String()

    val firestore = Firestore()

    val assignedMemberDetailList: LiveData<ArrayList<User>>
        get() = _assignedMemberDetailList

    fun setCardName(name: String) {
        cardName = name
    }

    fun setBoardDetails(board: Board) {
        boardDetails = board
    }

    fun getBoardDetails(): Board? {
        return this.boardDetails
    }

    fun setSelectedColor(color: String) {
        selectedColor = color
    }

    fun getSelectedColor(): String {
        return this.selectedColor
    }

    fun setTaskListPosition(position: Int) {
        taskListPosition = position
    }

    fun getTaskListPosition(): Int {
        return this.taskListPosition
    }

    fun setCardPosition(position: Int) {
        cardPosition = position
    }

    fun getCardPosition(): Int {
        return this.cardPosition
    }

    fun setAssignedMembers(members: ArrayList<User>) {
        _assignedMemberDetailList.value = members
    }

    fun setSelectedDueDate(dueDate: Long) {
        selectedDueDateMilis = dueDate
    }

    fun getSelectedDueDate(): Long {
        return this.selectedDueDateMilis
    }

    fun updateCardDetails() {
        val card = Card(
            cardName,
            boardDetails?.taskList?.get(taskListPosition)?.cards?.get(cardPosition)?.createdBy!!,
            boardDetails?.taskList?.get(taskListPosition)?.cards?.get(cardPosition)?.assignedTo!!,
            selectedColor,
            selectedDueDateMilis
        )

        val taskList: ArrayList<Task> = boardDetails?.taskList!!
        taskList.removeAt(taskList.size - 1)

        boardDetails?.taskList?.get(taskListPosition)?.cards!![cardPosition] = card
        taskListUpdated = firestore.addUpdateTaskList(boardDetails!!)
    }

    fun deleteCard() {
        val cardsList: ArrayList<Card> = boardDetails?.taskList!![taskListPosition].cards
        cardsList.removeAt(cardPosition)

        val taskList: ArrayList<Task> = boardDetails?.taskList!!
        taskList.removeAt(taskList.size - 1)

        taskList[taskListPosition].cards = cardsList
        taskListUpdated = firestore.addUpdateTaskList(boardDetails!!)
    }

}