package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _email: MutableLiveData<String> = MutableLiveData()
    private val _password: MutableLiveData<String> = MutableLiveData()
    private val _user: MutableLiveData<Boolean> = MutableLiveData()

    val email: LiveData<String>
        get() = _email

    val password: LiveData<String>
        get() = _password

    val user: LiveData<Boolean>
        get() = _user

    init {
        Log.i("SignInViewModel", "SignInView model created!")
        _user.value = false
    }

    fun signInUser(){
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(_email.value.toString(), _password.value.toString()).addOnCompleteListener{ task ->
            try{
                if(task.isSuccessful){
                    _user.value = true
                }
                else{
                    _user.value = false;
                    task.exception
                }
            }
            catch (e: FirebaseAuthInvalidCredentialsException){
                e.errorCode
            }
        }
    }

    fun setEmail(email: String){
        _email.value = email
    }

    fun setPassword(password: String){
        _password.value = password
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("SignInViewModel", "SignInView model destroyed!")
    }
}