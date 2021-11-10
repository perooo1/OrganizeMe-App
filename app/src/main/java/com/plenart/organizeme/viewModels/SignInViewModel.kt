package com.plenart.organizeme.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _user: MutableLiveData<Boolean> = MutableLiveData(false)
    private var email: String = String()
    private var password: String = String()

    val user: LiveData<Boolean>
        get() = _user

    fun getEmail(): String{
        return this.email
    }

    fun setEmail(email: String){
        this.email = email
    }

    fun getPassword():String{
        return this.password
    }

    fun setPassword(password: String){
        this.password = password
    }


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