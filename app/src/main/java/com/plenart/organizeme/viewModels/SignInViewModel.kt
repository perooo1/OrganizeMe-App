package com.plenart.organizeme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth

    var email: String = String()
    var password: String = String()
    private val _user: MutableLiveData<Boolean> = MutableLiveData(false)

    val user: LiveData<Boolean>
        get() = _user

    fun signInUser(){
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
            try{
                if(task.isSuccessful){
                    _user.value = true
                }
                else{
                    _user.value = false
                    task.exception
                }
            }
            catch (e: FirebaseAuthInvalidCredentialsException){
                e.errorCode
            }
        }
    }

}