package com.plenart.organizeme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.FragmentDrawerHostBinding
import com.plenart.organizeme.viewModels.DrawerHostViewModel


class DrawerHostFragment : Fragment() {

    private lateinit var binding: FragmentDrawerHostBinding
    private val viewModel: DrawerHostViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDrawerHostBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = binding.toolbarFragmentDrawerHost

        setupNavController()
        setupNavigationDrawer()
        setupListeners()
        setupObservers()

        toolbar.setupWithNavController(navHostFragment.navController, appBarConfiguration)
        binding.navView.setupWithNavController(navHostFragment.navController)

    }

    private fun setupObservers() {
        viewModel.signOutSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                val dirs = DrawerHostFragmentDirections.actionSecNavHostFragmentToIntroFragment()
                requireActivity().findNavController(R.id.main_content_navigation_component)
                    .navigate(dirs)
            }
        })
    }

    private fun setupListeners() {
        binding.navView.menu.findItem(R.id.introFragment).setOnMenuItemClickListener {
            viewModel.signOut()
            true
        }
    }

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.myProfileFragment,
                R.id.introFragment
            ), drawerLayout
        )
    }

    private fun setupNavController() {
        navHostFragment =
            childFragmentManager.findFragmentById(R.id.sec_nav_host) as NavHostFragment
    }

}