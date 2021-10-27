package com.plenart.organizeme.viewModels

import androidx.lifecycle.*
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    private val _boardsList: MutableLiveData<ArrayList<Board>> = MutableLiveData()
    private var userName = String()
    private val firestore = Firestore()

    val user: LiveData<User>
        get() = _user

    val boardsList: LiveData<ArrayList<Board>>
        get() = _boardsList

    fun getUserName(): String{
        return this.userName
    }

    fun loadUserData(){
        viewModelScope.launch {
            _user.value = firestore.loadUserData()
            userName = _user.value!!.name
        }
    }

    fun getBoardsList(){
        viewModelScope.launch(Dispatchers.IO) {
            _boardsList.postValue(firestore.getBoardsList())
        }
    }

}