package com.plenart.organizeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySignInBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.User

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding;
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_in)                     deprecated
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root);

        setUpActionBar()

        binding.btnSignInSignInActivity.setOnClickListener{
            singInUser();
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity);
        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp);
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener{onBackPressed();}

    }


    private fun singInUser(){

        auth = FirebaseAuth.getInstance();

        val email: String = binding.etEmailSignInActivity.text.toString().trim();
        val password: String = binding.etPasswordSignInActivity.text.toString();

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait));
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
                hideProgressDialog();
                if(task.isSuccessful){
                   FirestoreClass().signInUser(this);
                    val user = auth.currentUser;
                    //startActivity(Intent(this,MainActivity::class.java))
                }
                else{
                    Log.d("TAG", "signInWithEmailFail")
                    Toast.makeText(this, "Auth for login failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    fun signInSuccess(loggedInUser: User) {
        hideProgressDialog();
        startActivity(Intent(this, MainActivity::class.java))
        finish();
    }

    private fun validateForm(email: String,password: String): Boolean {
        return when{
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