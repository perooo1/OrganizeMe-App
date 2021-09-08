package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val _boardDetails: MutableLiveData<Board>? = MutableLiveData()
    private val _assignedMemberDetailList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private val _taskAddedUpdated: MutableLiveData<Boolean> = MutableLiveData()

    private val _taskList: MutableLiveData<ArrayList<Task>> = MutableLiveData()

    val firestore = Firestore()

    val boardDetails: LiveData<Board>?
        get() = _boardDetails

    val assignedMemberDetailList: LiveData<ArrayList<User>>
        get() = _assignedMemberDetailList

    val taskAddedUpdated: LiveData<Boolean>
        get() = _taskAddedUpdated

    val taskList: LiveData<ArrayList<Task>>
        get() = _taskList

    init {
        Log.i("TaskListActivity", "TaskListViewModel created!")
    }

    fun getBoardDetails(docID: String) {
        viewModelScope.launch {
            _boardDetails?.value = firestore.getBoardDetails(docID)
            setTaskList()
        }
    }

    private fun setTaskList() {
        _taskList.value = _boardDetails?.value?.taskList
    }

    fun getAssignedMembersListDetails() {
        viewModelScope.launch {
            _assignedMemberDetailList.value =
                firestore.getAssignedMembersListDetails(_boardDetails?.value?.assignedTo!!)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("TaskListActivity", "TaskListViewModel model destroyed!")
    }
}