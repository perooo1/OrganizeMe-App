package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth;

    private val _name: MutableLiveData<String> = MutableLiveData()
    private val _email: MutableLiveData<String> = MutableLiveData()
    private val _password: MutableLiveData<String> = MutableLiveData()
    private val _userRegisterSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val name: LiveData<String>
        get() = _name

    val email: LiveData<String>
        get() = _email

    val password: LiveData<String>
        get() = _password

    val userRegisterSuccess: LiveData<Boolean>
        get() = _userRegisterSuccess

    fun registerUser() {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(_email.value.toString(), _password.value.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    viewModelScope.launch(Dispatchers.IO) {

                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, _name.value.toString(), registeredEmail)
                        _userRegisterSuccess.postValue(Firestore().registerUser(user))
                        Log.d(
                            "SignUpViewModel",
                            "createUserWithEmailAndPassword Success - ${_userRegisterSuccess.value.toString()}"
                        )
                    }

                } else {
                    Log.d("SignUpViewModel", "createUserWithEmailAndPassword Failed")
                }
            }

    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}