package com.plenart.organizeme.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySignInBinding
import com.plenart.organizeme.viewModels.SignInViewModel

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        initObservers()
        initListeners()
        getEmail()
        getPassword()

    }

    private fun initListeners(){
        binding.btnSignInSignInActivity.setOnClickListener(){
            viewModel.signInUser();
        }
    }

    private fun getEmail() = with(binding.etEmailSignInActivity) {
        this.doAfterTextChanged {
            viewModel.setEmail(it.toString())
        }

    }

    private fun getPassword() {
        binding.etPasswordSignInActivity.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }
    }

    private fun initObservers() {
        initEmail()
        initPassword()
        initUser()
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener{onBackPressed()}

    }

    private fun initEmail(){
        viewModel.email?.observe(this, Observer { newEmail ->
            if(newEmail == null || !newEmail.contains('@')){
                showErrorSnackBar("Please enter email")
            }
        });
    }

    private fun initPassword(){
        viewModel.password?.observe(this, Observer { newPassword ->
            if(newPassword == null){
                showErrorSnackBar("Please enter a password")
            }
        })
    }

    private fun initUser(){
        viewModel.user?.observe(this, Observer { newUser ->
            if(newUser != null){
                showProgressDialog(resources.getString(R.string.please_wait))
                signInSuccess()
            }
            else{
                Log.i("initUser","newUser == null");
            }
        } )
    }

    private fun signInSuccess() {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}