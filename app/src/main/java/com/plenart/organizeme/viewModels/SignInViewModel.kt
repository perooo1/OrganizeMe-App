package com.plenart.organizeme.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User

class SignInViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth;

    var emailLiveData: MutableLiveData<String>? = null;
    var passwordLiveData: MutableLiveData<String>? = null;
    var userLiveData: MutableLiveData<User>? = null;

    init {
        Log.i("SignInViewModel", "SignInView model created!");
    }

    fun singInUser(){

        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(emailLiveData as String, passwordLiveData as String).addOnCompleteListener{ task ->

            if(task.isSuccessful){
                userLiveData?.value = Firestore().loadUserDataNEW();
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