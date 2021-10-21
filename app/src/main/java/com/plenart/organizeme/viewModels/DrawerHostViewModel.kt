package com.plenart.organizeme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.firebase.Firestore

class DrawerHostViewModel : ViewModel() {

    private val _signOutSuccess: MutableLiveData<Boolean> = MutableLiveData(false)

    val signOutSuccess: LiveData<Boolean>
        get() = _signOutSuccess

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        if (Firestore().getCurrentUserID().isEmpty()) {
            _signOutSuccess.value = true
        }
    }


}