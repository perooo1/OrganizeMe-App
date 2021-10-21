package com.plenart.organizeme.viewModels

import androidx.lifecycle.*
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val _boardDetails: MutableLiveData<Board> = MutableLiveData()
    private val _assignedMemberDetailList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private val _taskAddedUpdated: MutableLiveData<Boolean> = MutableLiveData()
    private val _taskList = Transformations.map(_boardDetails) { it.taskList }
    val firestore = Firestore()

    val boardDetails: LiveData<Board>
        get() = _boardDetails

    val assignedMemberDetailList: LiveData<ArrayList<User>>
        get() = _assignedMemberDetailList

    val taskAddedUpdated: LiveData<Boolean>
        get() = _taskAddedUpdated

    val taskList: LiveData<ArrayList<Task>>
        get() = _taskList

    fun getBoardDetails(docID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _boardDetails.postValue(firestore.getBoardDetails(docID))
        }
    }

    fun getAssignedMembersListDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _assignedMemberDetailList.postValue(
                firestore.getAssignedMembersListDetails(
                    _boardDetails.value?.assignedTo!!
                )
            )
        }
    }

}