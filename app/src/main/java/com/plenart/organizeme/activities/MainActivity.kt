package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.databinding.AppBarMainBinding
import com.plenart.organizeme.databinding.MainContentBinding
import com.plenart.organizeme.databinding.NavHeaderMainBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainActivityBinding: ActivityMainBinding;
    private lateinit var appBarMainBinding: AppBarMainBinding;
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding;
    private lateinit var mainContentBinding: MainContentBinding;

    private lateinit var mUserName: String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        setUpActionBar();
        mainActivityBinding.navView.setNavigationItemSelectedListener(this);

        FirestoreClass().loadUserData(this,true);

        appBarMainBinding.fabCreateBoard.setOnClickListener{
            Log.i("dodir fab","radi dodir");
            startActivity(Intent(this,CreateBoardActivity::class.java));
        }

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

    fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User, readBoardsList: Boolean) {
        navHeaderMainBinding = NavHeaderMainBinding.inflate(layoutInflater);
        mUserName = loggedInUser.name;

        Glide.with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navHeaderMainBinding.navUserImg);

        navHeaderMainBinding.tvUsername.text = loggedInUser.name;

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait));
            FirestoreClass().getBoardsList(this);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this);
        }
        else{
            Log.e("MainOnActivityResultErr", "error")
        }
    }


    fun populateBoardsListToUI(boardsList: ArrayList<Board>){

        mainContentBinding = MainContentBinding.inflate(layoutInflater);
        hideProgressDialog();

        if(boardsList.size > 0){
            mainContentBinding.rvBoards.visibility = View.VISIBLE;
            mainContentBinding.tvNoBoardsAvailable.visibility = View.GONE;

            mainContentBinding.rvBoards.layoutManager = LinearLayoutManager(this);
            mainContentBinding.rvBoards.setHasFixedSize(true);

            val adapter = BoardItemsAdapter(this@MainActivity, boardsList);
            mainContentBinding.rvBoards.adapter = adapter;
            Log.i("POPUI","Board adapter size: ${adapter.itemCount}");
            //adapter.notifyDataSetChanged();
        }

        else{
            mainContentBinding.rvBoards.visibility = View.GONE;
            mainContentBinding.tvNoBoardsAvailable.visibility = View.VISIBLE;
        }


    }

    fun klikniMe(view: View) {
        Log.i("klik","klik")

        val intent = Intent(this,CreateBoardActivity::class.java);
        intent.putExtra(Constants.NAME, mUserName);

        startActivity(intent);
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11;
    }
}
