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
    private val _boardsList: MutableLiveData<ArrayList<Board>> = MutableLiveData();

    val firestore = Firestore()
    val user: LiveData<User>
        get() = _user

    val boardsList: LiveData<ArrayList<Board>>
        get() = _boardsList


    init {
        Log.i("MainActivity", "MainActivityViewModel created!")
    }

    fun loadUserData(){
        _user.value = firestore.loadUserDataNEW()
    }

    fun getBoardsList(){
        //TODO implement
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainActivityViewModel", "MainActivityViewModel model destroyed!")
    }
}