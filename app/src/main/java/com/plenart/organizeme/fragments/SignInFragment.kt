package com.plenart.organizeme.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
        getEmail()
        getPassword()

    }

    private fun initListeners() {
        binding.btnSignInSignInActivity.setOnClickListener() {
            if (!viewModel.getEmail().contains('@')) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.email_hint),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                if (viewModel.getPassword().isEmpty() || viewModel.getPassword().length < 6) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.password_min_characters),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    viewModel.signInUser()
                }

            }
        }
    }

    private fun getEmail() = binding.etEmailSignInActivity.doOnTextChanged { newEmail, _, _, _ ->
        viewModel.setEmail(newEmail.toString())
    }

    private fun getPassword() =
        binding.etPasswordSignInActivity.doOnTextChanged { newPassword, _, _, _ ->
            viewModel.setPassword(newPassword.toString())
        }

    private fun initObservers() {
        userSignInSuccess()
    }

    private fun userSignInSuccess() {
        viewModel.user.observe(viewLifecycleOwner, Observer { newUser ->
            if (newUser) {
                signInSuccess()
            } else {
                Log.i("initUser", "newUser == false")
            }

        })
    }

    private fun signInSuccess() {
        findNavController().navigate(R.id.action_signInFragment_to_secNavHostFragment)
    }

}