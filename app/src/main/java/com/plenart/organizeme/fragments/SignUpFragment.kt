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
import androidx.navigation.fragment.findNavController
import com.plenart.organizeme.R
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

    private fun getName()=with(binding.etNameSignUpActivity){
        this.doAfterTextChanged {
            viewModel.setName(text.toString())
        }
    }

    private fun getEmail()=with(binding.etEmailSignUpActivity) {
        this.doAfterTextChanged {
            viewModel.setEmail(text.toString())
        }
    }

    private fun getPassword()=with(binding.etPasswordSignUpActivity) {
        this.doAfterTextChanged {
            viewModel.setPassword(text.toString())
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
        viewModel.password.observe(viewLifecycleOwner, Observer { newPassword ->
            if(newPassword == null ){
                Toast.makeText(requireContext(), "Please enter a password", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                binding.etPasswordSignUpActivity.text.toString()
            }
        })
    }

    private fun initEmail() {
        viewModel.email.observe(viewLifecycleOwner, Observer { newEmail ->
            if(newEmail == null ){
                Toast.makeText(requireContext(), "Email must contain @ sign", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                binding.etEmailSignUpActivity.text.toString().trim{it <=' '}
            }
        })
    }

    private fun initName() {
        viewModel.name.observe(viewLifecycleOwner, Observer { newName ->
            if(newName == null ){
                Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                binding.etNameSignUpActivity.text.toString().trim{it <=' '}
            }
        })
    }

    private fun userRegisteredSuccess(){
        Toast.makeText(activity, " you have successfully registered the email", Toast.LENGTH_LONG).show()
        viewModel.signOut()
        findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
    }


}