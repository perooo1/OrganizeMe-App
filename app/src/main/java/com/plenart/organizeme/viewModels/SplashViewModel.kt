package com.plenart.organizeme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plenart.organizeme.firebase.Firestore

class SplashViewModel : ViewModel() {

    private val currentUserID: String = Firestore().getCurrentUserID()
    private val _userSignedIn: MutableLiveData<Boolean> = MutableLiveData(false)

    val userSignedIn: LiveData<Boolean>
        get() = _userSignedIn

    fun isUserSignedIn() {
        if (currentUserID.isNotEmpty()) {
            _userSignedIn.value = true
        }

    }

}