package com.plenart.organizeme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.FragmentDrawerHostBinding


class DrawerHostFragment : Fragment() {

    private lateinit var binding: FragmentDrawerHostBinding
    private lateinit var toolbar: Toolbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDrawerHostBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = binding.toolbarFragmentDrawerHost

        setupNavController()
        setupNavigationDrawer()

        toolbar.setupWithNavController(navController,appBarConfiguration)
        binding.navView.setupWithNavController(navController)

    }

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment,R.id.myProfileFragment),drawerLayout)
    }

    private fun setupNavController() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.sec_nav_host) as NavHostFragment
        navController = navHostFragment.navController

    }

}