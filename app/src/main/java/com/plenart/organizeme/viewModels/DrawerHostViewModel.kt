package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.firebase.Firestore

class DrawerHostViewModel : ViewModel() {

    private val _signOutSuccess: MutableLiveData<Boolean> = MutableLiveData(false)

    val signOutSuccess: LiveData<Boolean>
        get() = _signOutSuccess

    init {
        Log.i("DrawerHost", "DrawerHostViewModel created!")
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        if (Firestore().getCurrentUserID().isEmpty()) {
            _signOutSuccess.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("DrawerHostViewModel", "DrawerHostViewModel model destroyed!")
    }


}