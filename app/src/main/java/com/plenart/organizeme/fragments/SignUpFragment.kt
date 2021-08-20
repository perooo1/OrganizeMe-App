package com.plenart.organizeme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.databinding.FragmentSignUpBinding
import com.plenart.organizeme.viewModels.SignUpViewModel


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
        getName()
        getEmail()
        getPassword()

    }

    private fun initListeners(){
        binding.btnSignUpSignUpActivity.setOnClickListener{
            viewModel.registerUser()
        }
    }

    private fun initObservers() {
        initName()
        initEmail()
        initPassword()
        initUserRegister()
    }

    private fun getName(){
        binding.etNameSignUpActivity.doAfterTextChanged {
            viewModel.setName(it.toString())
        }
    }

    private fun getEmail() {
        binding.etEmailSignUpActivity.doAfterTextChanged {
            viewModel.setEmail(it.toString())
        }
    }

    private fun getPassword() {
        binding.etEmailSignUpActivity.doAfterTextChanged {
            viewModel.setPassword(it.toString())
        }
    }

    private fun initUserRegister() {
        viewModel.userRegisterSuccess.observe(viewLifecycleOwner, Observer {
            if(it){
                userRegisteredSuccess()
            }
            else{
                Toast.makeText(activity,"registration failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initPassword() {
        viewModel.password?.observe(viewLifecycleOwner, Observer { newPassword ->
            if(newPassword == null ){
                //showErrorSnackBar("Please enter a password")
            }
            else{
                binding.etPasswordSignUpActivity.text.toString()
            }
        })
    }

    private fun initEmail() {
        viewModel.email?.observe(viewLifecycleOwner, Observer { newEmail ->
            if(newEmail == null ){
                //showErrorSnackBar("Please enter a email")
            }
            else{
                binding.etEmailSignUpActivity.text.toString().trim{it <=' '}
            }
        })
    }

    private fun initName() {
        viewModel.name?.observe(viewLifecycleOwner, Observer { newName ->
            if(newName == null ){
                //showErrorSnackBar("Please enter a name")
            }
            else{
                binding.etNameSignUpActivity.text.toString().trim{it <=' '}
            }
        })
    }

    private fun setUpActionBar(){
        /*
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener{onBackPressed()}
        */
    }

    private fun userRegisteredSuccess(){
        Toast.makeText(activity, " you have successfully registered the email", Toast.LENGTH_LONG).show()
        //hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
    }


}