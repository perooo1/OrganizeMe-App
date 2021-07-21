package com.plenart.organizeme.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySignInBinding
import com.plenart.organizeme.viewModels.SignInViewModel

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding;
    private lateinit var viewModel: SignInViewModel;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(layoutInflater);
        setContentView(binding.root);

        setUpActionBar();

        Log.i("SignInActivity", "Called ViewModelProvider");
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java);

        emailObserver();
        passwordObserver();
        userObserver();

        binding.btnSignInSignInActivity.setOnClickListener{
            viewModel.singInUser();
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

    private fun emailObserver(){
        viewModel.email?.observe(this, Observer { newEmail ->
            if(newEmail == null || !newEmail.contains('@')){
                showErrorSnackBar("Please enter email");
                Log.d("emailObserver","This has been called!")
            }
        });
    }

    private fun passwordObserver(){
        viewModel.password?.observe(this, Observer { newPassword ->
            if(newPassword == null){
                showErrorSnackBar("Please enter a password");
            }
        })
    }

    private fun userObserver(){
        viewModel.user?.observe(this, Observer { newUser ->
            if(newUser != null){
                signInSuccess();
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait));
            }
        } )
    }

    fun signInSuccess() {
        hideProgressDialog();
        startActivity(Intent(this, MainActivity::class.java))
        finish();
    }

}