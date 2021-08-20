package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.viewModels.MainActivityViewModel
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainActivityBinding.root
        setContentView(view)

        toolbar = mainActivityBinding.toolbarMainActivity2
        setSupportActionBar(toolbar)

        drawerLayout = mainActivityBinding.drawerLayout
        val navView: NavigationView = mainActivityBinding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_content_navigation_component) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(),drawerLayout)

        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)


        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserID = Firestore().getCurrentUserID()

            if(currentUserID.isNotEmpty()){
                navController.navigate(R.id.action_splashFragment_to_mainFragment)
            }
            else{
                navController.navigate(R.id.action_splashFragment_to_introFragment)
            }

        },2500)

    }

    private fun toggleDrawer(){
        if(mainActivityBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            mainActivityBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    override fun onBackPressed() {
        if(mainActivityBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
           doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        /*
        when(item.itemId){
            R.id.nav_home ->{
                toggleDrawer()
            }
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START)
        */
        return true;

    }

    private fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User, readBoardsList: Boolean = false) {

        val nav_user_img: CircleImageView = findViewById(R.id.nav_user_img)
        val tv_username: TextView = findViewById(R.id.tv_username)

        Glide.with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_img)

        tv_username.text = loggedInUser.name

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))

            viewModel.getBoardsList()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            viewModel.loadUserData()

        }
        else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            viewModel.getBoardsList()
        }
        else{
            Log.e("MainOnActivityResultErr", "error")
        }
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}
