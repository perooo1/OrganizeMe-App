package com.plenart.organizeme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
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

    private fun initListeners() {
        binding.btnSignUpSignUpActivity.setOnClickListener {
            viewModel.registerUser()
        }
    }

    private fun initObservers() {
        initName()
        initEmail()
        initPassword()
        initUserRegister()
    }

    private fun getName() = binding.etNameSignUpActivity.doOnTextChanged { newName, _, _, _ ->
        viewModel.setName(newName.toString())
    }

    private fun getEmail() = binding.etEmailSignUpActivity.doOnTextChanged { newEmail, _, _, _ ->
        viewModel.setEmail(newEmail.toString())
    }

    private fun getPassword() = binding.etPasswordSignUpActivity.doOnTextChanged { newPassword, _, _, _ ->
        viewModel.setPassword(newPassword.toString())
    }

    private fun initUserRegister() {
        viewModel.userRegisterSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                userRegisteredSuccess()
            } else {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.registration_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun initPassword() {
        viewModel.password.observe(viewLifecycleOwner, Observer { newPassword ->
            if (newPassword == null) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.please_provide_password),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun initEmail() {
        viewModel.email.observe(viewLifecycleOwner, Observer { newEmail ->
            if (newEmail == null) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.email_hint),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun initName() {
        viewModel.name.observe(viewLifecycleOwner, Observer { newName ->
            if (newName == null) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.please_provide_your_name),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun userRegisteredSuccess() {
        Toast.makeText(
            activity,
            resources.getString(R.string.registration_successful),
            Toast.LENGTH_LONG
        ).show()
        viewModel.signOut()
        findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
    }

}