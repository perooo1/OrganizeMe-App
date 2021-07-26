package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User

class MainActivityViewModel: ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    private val _userName: MutableLiveData<String> = MutableLiveData()
    private val _boardsList: MutableLiveData<ArrayList<Board>> = MutableLiveData()

    val firestore = Firestore()

    val user: LiveData<User>
        get() = _user

    val userName: LiveData<String>
        get() = _userName

    val boardsList: LiveData<ArrayList<Board>>
        get() = _boardsList

    init {
        Log.i("MainActivity", "MainActivityViewModel created!")
    }

    suspend fun loadUserData(){
        _user.value = firestore.loadUserDataNEW()
        setUserName()
    }

    private fun setUserName(){
        _userName.value = _user.value?.name
    }

    suspend fun getBoardsList(){
        _boardsList.value = firestore.getBoardsListNEW()
    }

    fun checkBoardsList(): Boolean {
        return boardsList == null
    }

    fun checkUser(): Boolean{
        return user == null
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainActivityViewModel", "MainActivityViewModel model destroyed!")
    }

}