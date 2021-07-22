package com.plenart.organizeme.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySignUpBinding
import com.plenart.organizeme.viewModels.SignUpViewModel

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding;
    private lateinit var viewModel: SignUpViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater);
        setContentView(binding.root);

        setUpActionBar();

        Log.i("SignUpActivity", "Called ViewModelProvider");
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java);

        initObservers()
        getName();
        getEmail();
        getPassword();

        binding.btnSignUpSignUpActivity.setOnClickListener{
            viewModel.registerUser();
        }
    }

    private fun initObservers() {
        nameObserver();
        emailObserver();
        passwordObserver();
        registerObserver();
    }

    private fun getName(){
        binding.etNameSignUpActivity.doAfterTextChanged {
            viewModel.setName(it.toString());
        }
    }

    private fun getEmail() {
        binding.etEmailSignUpActivity.doAfterTextChanged {
            viewModel.setEmail(it.toString());
        }
    }

    private fun getPassword() {
        binding.etEmailSignUpActivity.doAfterTextChanged {
            viewModel.setPassword(it.toString());
        }
    }

    private fun registerObserver() {
        viewModel.userRegisterSuccess.observe(this, Observer {
            if(it){
                userRegisteredSuccess();
            }
            else{
                Toast.makeText(this,"registration failed", Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun passwordObserver() {
        viewModel.password?.observe(this, Observer { newPassword ->
            if(newPassword == null ){
                showErrorSnackBar("Please enter a password");
            }
            else{
                binding.etPasswordSignUpActivity.text.toString();       //does this make any sense?
            }
        })
    }

    private fun emailObserver() {
        viewModel.email?.observe(this, Observer { newEmail ->
            if(newEmail == null ){
                showErrorSnackBar("Please enter a email");
            }
            else{
                binding.etEmailSignUpActivity.text.toString().trim{it <=' '}
            }
        })
    }

    private fun nameObserver() {
        viewModel.name?.observe(this, Observer { newName ->
            if(newName == null ){
                showErrorSnackBar("Please enter a name");
            }
            else{
                binding.etNameSignUpActivity.text.toString().trim{it <=' '}
            }
        })
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

    fun userRegisteredSuccess(){
        Toast.makeText(this, " you have successfully registered the email", Toast.LENGTH_LONG).show();
        hideProgressDialog();
        FirebaseAuth.getInstance().signOut();
        finish();
    }

}