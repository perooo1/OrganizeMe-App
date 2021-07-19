package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User

class SignInViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth;

    private val _email: MutableLiveData<String>? = MutableLiveData();    // initalize with constructor!
    private val _password: MutableLiveData<String>? = MutableLiveData();
    private val _user: MutableLiveData<User>? = MutableLiveData();

    val email: LiveData<String>?
        get() = _email;

    val password: LiveData<String>?
        get() = _password;

    val user: LiveData<User>?
        get() = _user;

    init {
        Log.i("SignInViewModel", "SignInView model created!");
    }

    fun singInUser(){

        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email as String, password as String).addOnCompleteListener{ task ->     //CAREFUL, SHOULD BE _EMAIL??

            if(task.isSuccessful){
                _user?.value = Firestore().loadUserDataNEW();
                val user = auth.currentUser;

            }
            else{
                Log.d("TAG", "signInWithEmailFail")
                //Toast.makeText(context, "Auth for login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onCleared() {
        super.onCleared();
        Log.i("SignInViewModel", "SignInView model destroyed!");
    }
}