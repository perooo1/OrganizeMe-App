package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.databinding.AppBarMainBinding
import com.plenart.organizeme.databinding.NavHeaderMainBinding
import com.plenart.organizeme.firebase.FirestoreClass

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainActivityBinding: ActivityMainBinding;
    private lateinit var appBarMainBinding: AppBarMainBinding;
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        setUpActionBar();
        mainActivityBinding.navView.setNavigationItemSelectedListener(this);

        FirestoreClass().loadUserData(this);

    }


    private fun setUpActionBar(){
        appBarMainBinding = AppBarMainBinding.inflate(layoutInflater)
        setSupportActionBar(appBarMainBinding.toolbarMainActivity)
        appBarMainBinding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu);      //navigation icon not showing up when set through code like this?

        appBarMainBinding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer();
        }

    }

    private fun toggleDrawer(){
        if(mainActivityBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            mainActivityBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    override fun onBackPressed() {
        if(mainActivityBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
           doubleBackToExit();
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE);
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut();
                val intent = Intent(this, IntroActivity::class.java);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User) {
        navHeaderMainBinding = NavHeaderMainBinding.inflate(layoutInflater);

        Glide.with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navHeaderMainBinding.navUserImg);

        navHeaderMainBinding.tvUsername.text = loggedInUser.name;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this);
        }
        else{
            Log.e("Main onActivityResult Error", "error")
        }
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11;
    }

}