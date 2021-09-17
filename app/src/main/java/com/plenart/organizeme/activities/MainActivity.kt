package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.viewModels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbarMainActivity
        setSupportActionBar(toolbar)

        setupNavController()
        setupActionBarWithNavController(navController)

        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserID = Firestore().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                navController.navigate(R.id.action_splashFragment_to_secNavHostFragment)
            } else {
                navController.navigate(R.id.action_splashFragment_to_introFragment)
            }
        }, 2500)

    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_content_navigation_component) as NavHostFragment
        navController = navHostFragment.navController

        setupNavControllerListeners()
    }

    private fun setupNavControllerListeners() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment || destination.id == R.id.introFragment || destination.id == R.id.secNavHostFragment ) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
            if(destination.id == R.id.introFragment) {
                FirebaseAuth.getInstance().signOut()
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            viewModel.loadUserData()

        } else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            viewModel.getBoardsList()
        } else {
            Log.e("MainOnActivityResultErr", "error")
        }
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}
