package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User

class TaskListViewModel: ViewModel() {

    private val _boardDetails: MutableLiveData<Board>? = MutableLiveData()
    private val _boardDocumentID: MutableLiveData<String> = MutableLiveData()
    private val _assignedMemberDetailList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private val _taskAddedUpdated: MutableLiveData<Boolean> = MutableLiveData()

    val firestore = Firestore()

    val boardDetails: LiveData<Board>?
        get() = _boardDetails

    val boardDocumentID: LiveData<String>
        get() = _boardDocumentID

    val assignedMemberDetailList: LiveData<ArrayList<User>>
        get() = _assignedMemberDetailList

    val taskAddedUpdated: LiveData<Boolean>
        get() =  _taskAddedUpdated

    init {
        Log.i("TaskListActivity", "TaskListViewModel created!")
    }

    fun setBoardDocumentID(id: String){
        _boardDocumentID.value = id
    }

    suspend fun getBoardDetails(){
        Log.i("getBoardDetails","_boardDocumentID jest: ${_boardDocumentID?.value.toString()}")
        _boardDetails?.value = firestore.getBoardDetailsNEW(_boardDocumentID.value.toString())
    }

    suspend fun getAssignedMembersListDetails(){
        Log.i("getBoardDetails","_boardDetails jest: ${_boardDetails?.value.toString()}")
        _assignedMemberDetailList.value = firestore.getAssignedMembersListDetailsNEW(_boardDetails?.value?.assignedTo!!)
    }

    fun checkBoardDetails(): Boolean{
        return _boardDetails == null
    }

    fun checkAssignedMembers(): Boolean{
        return _assignedMemberDetailList.value.isNullOrEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("TaskListActivity", "TaskListViewModel model destroyed!")
    }
}