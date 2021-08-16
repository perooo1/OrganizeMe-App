package com.plenart.organizeme.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.plenart.organizeme.R
import com.plenart.organizeme.activities.SignInActivity
import com.plenart.organizeme.activities.SignUpActivity
import com.plenart.organizeme.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {

    private lateinit var binding: FragmentIntroBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

    }

    private fun initListeners() {
        binding.btnSignUpIntro.setOnClickListener{

            val fragment = SignUpFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_intro,fragment)
                .commit()
            //startActivity(Intent(activity, SignUpActivity::class.java))
        }

        binding.btnSignInIntro.setOnClickListener{

            val fragment = SignInFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_intro,fragment)
                .commit()

            //startActivity(Intent(activity, SignInActivity::class.java))
        }
    }


}