package com.plenart.organizeme.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.FragmentSignInBinding
import com.plenart.organizeme.viewModels.SignInViewModel


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpActionBar()
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
        /*
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener{onBackPressed()}
        */
    }

    private fun initEmail(){
        viewModel.email?.observe(viewLifecycleOwner, Observer { newEmail ->
            if(newEmail == null || !newEmail.contains('@')){
                //showErrorSnackBar("Please enter email")
            }
        });
    }

    private fun initPassword(){
        viewModel.password?.observe(viewLifecycleOwner, Observer { newPassword ->
            if(newPassword == null){
                //showErrorSnackBar("Please enter a password")
            }
        })
    }

    private fun initUser(){
        viewModel.user?.observe(viewLifecycleOwner, Observer { newUser ->
            if(newUser != null){
                //showProgressDialog(resources.getString(R.string.please_wait))
                signInSuccess()
            }
            else{
                Log.i("initUser","newUser == null");
            }
        } )
    }

    private fun signInSuccess() {
        //hideProgressDialog()
        val fragment = MainFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_sign_in,fragment)
            .commit()
    }

}