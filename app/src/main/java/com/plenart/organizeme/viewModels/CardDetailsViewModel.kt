package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User

class CardDetailsViewModel: ViewModel() {

    private val _boardDetails: MutableLiveData<Board>? = MutableLiveData()
    private val _assignedMemberDetailList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private val _taskListPosition: MutableLiveData<Int> = MutableLiveData()
    private val _cardPosition: MutableLiveData<Int> = MutableLiveData()
    private val _selectedColor: MutableLiveData<String> = MutableLiveData()
    private val _taskListUpdated: MutableLiveData<Boolean> = MutableLiveData()
    private val _selectedDueDateMilis: MutableLiveData<Long> = MutableLiveData()
    private val _cardName: MutableLiveData<String> = MutableLiveData()

    val firestore = Firestore()

    val boardDetails: LiveData<Board>?
        get() = _boardDetails

    val assignedMemberDetailList: LiveData<ArrayList<User>>
        get() = _assignedMemberDetailList

    val taskListPosition: LiveData<Int>
        get() = _taskListPosition

    val cardPosition: LiveData<Int>
        get() = _cardPosition

    val selectedColor: LiveData<String>
        get() = _selectedColor

    val taskListUpdated: LiveData<Boolean>
        get() = _taskListUpdated

    val selectedDueDateMilis: LiveData<Long>
        get() = _selectedDueDateMilis

    val cardName: LiveData<String>
        get() = _cardName

    init {
        Log.i("CardDetailsViewModel", "CardDetailsViewModel created!")
        _taskListPosition.value = -1
        _selectedColor.value = ""
        _selectedDueDateMilis.value = 0L
    }

    fun setCardName(name: String){
        _cardName.value = name
    }

    fun setBoardDetails(board: Board){
        _boardDetails?.value = board
    }

    fun setSelectedColor(color: String){
        _selectedColor.value = color
    }

    fun setTaskListPosition(position: Int){
        _taskListPosition.value = position
    }

    fun setCardPosition(position: Int){
        _cardPosition.value = position
    }

    fun setAssignedMembers(members: ArrayList<User>){
        _assignedMemberDetailList.value = members
    }

   fun setSelectedDueDate(dueDate: Long){
       _selectedDueDateMilis.value = dueDate
   }

    fun updateCardDetails(){
        val card = Card(_cardName.value.toString(),
            _boardDetails?.value?.taskList?.get(_taskListPosition.value!!)?.cards?.get(_cardPosition.value!!)?.createdBy!!,
            _boardDetails?.value?.taskList?.get(_taskListPosition.value!!)?.cards?.get(_cardPosition.value!!)?.assignedTo!!,
            _selectedColor.value.toString(),
            _selectedDueDateMilis.value!!
        )

        val taskList: ArrayList<Task> = _boardDetails!!.value!!.taskList
        taskList.removeAt(taskList.size -1);

        _boardDetails?.value?.taskList?.get(_taskListPosition.value!!)?.cards!![_cardPosition.value!!] = card
        _taskListUpdated.value = firestore.addUpdateTaskList(_boardDetails.value!!)
    }

    fun deleteCard(){
        val cardsList: ArrayList<Card> = _boardDetails?.value?.taskList!![_taskListPosition.value!!].cards
        cardsList.removeAt(_cardPosition.value!!)

        val taskList: ArrayList<Task> = _boardDetails?.value?.taskList!!
        taskList.removeAt(taskList.size - 1)

        taskList[_taskListPosition.value!!].cards = cardsList
        _taskListUpdated.value = firestore.addUpdateTaskList(_boardDetails.value!!)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("CardDetailsViewModel", "MainActivityViewModel model destroyed!")
    }
}