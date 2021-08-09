package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User
import kotlinx.coroutines.launch

class SignInViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _email: MutableLiveData<String> = MutableLiveData()
    private val _password: MutableLiveData<String> = MutableLiveData()
    private val _user: MutableLiveData<User> = MutableLiveData()

    val email: LiveData<String>
        get() = _email

    val password: LiveData<String>
        get() = _password

    val user: LiveData<User>
        get() = _user

    init {
        Log.i("SignInViewModel", "SignInView model created!")
    }

    fun singInUser(){
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(_email.value.toString(), _password.value.toString()).addOnCompleteListener{ task ->
            try{
                if(task.isSuccessful){
                    viewModelScope.launch {
                        _user?.postValue(Firestore().loadUserData())
                        val user = auth.currentUser
                        Log.d("signInUser", "signInWithEmail Success")
                    }
                }
                else{
                    Log.d("signInUser", "signInWithEmailFail")
                    task.exception;
                }
            }
            catch (e: FirebaseAuthException){
                e.errorCode
                Log.d("signInUser","the error code is  ${e.errorCode}")
            }
        }
    }

    fun setEmail(email: String){
        _email.value = email
    }

    fun setPassword(password: String){
        _password.value = password
    }

    fun checkUser(): Boolean{
        return user == null
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("SignInViewModel", "SignInView model destroyed!")
    }
}