package com.plenart.organizeme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.FragmentSplashBinding
import com.plenart.organizeme.viewModels.SplashViewModel

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.actionBar?.hide()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        viewModel.isUserSignedIn()
    }

    private fun initObservers() {
        viewModel.userSignedIn.observe(viewLifecycleOwner, Observer {
            if(it){
                requireActivity()
                    .findNavController(R.id.main_content_navigation_component)
                    .navigate(R.id.action_splashFragment_to_secNavHostFragment)
            }
            else{
                requireActivity()
                    .findNavController(R.id.main_content_navigation_component)
                    .navigate(R.id.action_splashFragment_to_introFragment)
            }
        })
    }

}