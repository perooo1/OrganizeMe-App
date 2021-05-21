package com.plenart.organizeme.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.plenart.organizeme.activities.BaseActivity
import com.plenart.organizeme.activities.MainActivity
import com.plenart.organizeme.activities.SignInActivity
import com.plenart.organizeme.activities.SignUpActivity
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance();


    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess();
        }
    }

    fun getCurrentUserID(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser;
        var currentUserID = "";
        if(currentUser != null){
            currentUserID = currentUser.uid;
        }

        return currentUserID;
    }

    fun signInUser(activity: Activity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)

                when(activity){
                    is SignInActivity ->{
                        if (loggedInUser != null) {
                            activity.signInSuccess(loggedInUser)
                        };
                    }
                    is MainActivity ->{
                        if (loggedInUser != null) {
                            activity.updateNavigationUserDetails(loggedInUser)
                        };
                    }
                }

            }.addOnFailureListener {

                    e ->
                Log.e("FirestoreSignInUser", "Error writing document", e);
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog();
                    }
                }
            }
    }


}