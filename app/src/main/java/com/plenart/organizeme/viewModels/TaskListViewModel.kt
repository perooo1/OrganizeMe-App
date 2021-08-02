package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import kotlinx.coroutines.launch

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

        viewModelScope.launch {
           getBoardDetails()
        }

    }

    fun setBoardDocumentID(id: String){
        _boardDocumentID.value = id;
    }

    suspend fun getBoardDetails(){
        _boardDetails?.value = firestore.getBoardDetailsNEW(_boardDocumentID.value.toString())
        //showProgressDialog(resources.getString(R.string.please_wait));

        //Firestore().getAssignedMembersListDetails(this, mBoardDetails.assignedTo);
        _assignedMemberDetailList.value =
            _boardDetails?.value?.assignedTo?.let { firestore.getAssignedMembersListDetailsNEW(it) }        //careful with null operators!
    }

    fun checkAssignedMembers(): Boolean{
        return _assignedMemberDetailList.value.isNullOrEmpty()
    }


    override fun onCleared() {
        super.onCleared()
        Log.i("TaskListActivity", "TaskListViewModel model destroyed!")
    }
}