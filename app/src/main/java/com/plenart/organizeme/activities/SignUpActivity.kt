package com.plenart.organizeme.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySignUpBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(binding.root);

        setUpActionBar();

        binding.btnSignUpSignUpActivity.setOnClickListener{
            registerUser();
        }

    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity);
        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp);
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener{onBackPressed();}

    }

    private fun registerUser(){
        val name: String = binding.etNameSignUpActivity.text.toString().trim{it <=' '}
        val email: String = binding.etEmailSignUpActivity.text.toString().trim{it <=' '}
        val password: String = binding.etPasswordSignUpActivity.text.toString();

        if(validateForm(name, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait));
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!;
                    val registeredEmail = firebaseUser.email!!;
                    val user = User(firebaseUser.uid,name, registeredEmail);
                    Firestore().registerUser(this,user);
                    hideProgressDialog();
                    finish();
                }
                else{
                    Toast.makeText(this,"registration failed",Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, " you have succesfully registered the email", Toast.LENGTH_LONG).show();
        hideProgressDialog();
        FirebaseAuth.getInstance().signOut();
        finish();
    }


    private fun validateForm(name: String, email: String,password: String): Boolean {
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name");
                false;
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter email");
                false;
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password");
                false;
            }
            else -> {true}
        }

    }

}