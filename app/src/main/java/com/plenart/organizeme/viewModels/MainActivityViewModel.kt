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

    fun loadUserData(){
        viewModelScope.launch {
            _user.value = firestore.loadUserData()
            setUserName()
        }
    }

    private fun setUserName(){
        _userName.value = _user.value?.name
    }

    fun getBoardsList(){
        viewModelScope.launch {
            _boardsList.value = firestore.getBoardsList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainActivityViewModel", "MainActivityViewModel model destroyed!")
    }

}