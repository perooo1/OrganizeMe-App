package com.plenart.organizeme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MembersViewModel : ViewModel() {

    private val _boardDetails: MutableLiveData<Board> = MutableLiveData()
    private val _assignedMemberDetailList: MutableLiveData<ArrayList<User>> = MutableLiveData()
    private val _memberAssignSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _member: MutableLiveData<User> = MutableLiveData()
    private val _email: MutableLiveData<String> = MutableLiveData()
    private var anyChangesMade: Boolean = false

    val firestore = Firestore()

    val boardDetails: LiveData<Board>
        get() = _boardDetails

    val assignedMemberDetailList: LiveData<ArrayList<User>>
        get() = _assignedMemberDetailList

    val memberAssignSuccess: LiveData<Boolean>
        get() = _memberAssignSuccess

    val member: LiveData<User>
        get() = _member

    val email: LiveData<String>
        get() = _email

    fun getAssignedMembersListDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _assignedMemberDetailList.postValue(
                firestore.getAssignedMembersListDetails(
                    _boardDetails.value?.assignedTo!!
                )
            )
        }
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setBoardDetails(details: Board) {
        _boardDetails.value = details
    }

    fun setAnyChangesMade(changesMade: Boolean) {
        anyChangesMade = changesMade
    }

    fun setMemberFromDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            _member.postValue(firestore.getMemberDetails(_email.value.toString()))
        }
    }

}