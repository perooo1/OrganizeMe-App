package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.databinding.AppBarMainBinding
import com.plenart.organizeme.databinding.MainContentBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.MainActivityViewModel
import com.plenart.organizeme.viewModels.SignInViewModel
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var appBarMainBinding: AppBarMainBinding
    private lateinit var mainContentBinding: MainContentBinding
    private lateinit var viewModel: MainActivityViewModel

    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        setUpActionBar();

        Log.i("MainActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        initObservers()

        mainActivityBinding.navView.setNavigationItemSelectedListener(this);
        /////////

        viewModel.loadUserData();

        ///////
        //Firestore().loadUserData(this,true);        //this belongs in a viewModel

        appBarMainBinding.fabCreateBoard.setOnClickListener{
            val intent = Intent(this,CreateBoardActivity::class.java);
            intent.putExtra(Constants.NAME, mUserName);

            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE);
        }

    }

    private fun initObservers() {
        userObserver()
    }

    private fun userObserver() {

        viewModel.user?.observe(this, Observer { newUser ->
            if(newUser != null){
                updateNavigationUserDetails(newUser,true)
            }
            else{
                Log.i("MainActivityUsrObs","the else block")
            }
        } )
    }


    private fun setUpActionBar(){
        appBarMainBinding = mainActivityBinding.appBarMainIncluded;

        setSupportActionBar(appBarMainBinding.toolbarMainActivity)
        appBarMainBinding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu);

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
            R.id.nav_home ->{
                toggleDrawer();
            }
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

    fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User, readBoardsList: Boolean = false) {

        val nav_user_img: CircleImageView = findViewById(R.id.nav_user_img);
        val tv_username: TextView = findViewById(R.id.tv_username);

        mUserName = loggedInUser.name;

        Glide.with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_img);

        tv_username.text = loggedInUser.name;

        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait));
            Firestore().getBoardsList(this);        //this belongs in viewModel
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            viewModel.loadUserData();
            //Firestore().loadUserData(this);                     //this belongs in viewModel
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            Firestore().getBoardsList(this)                     //this belongs in viewModel
        }
        else{
            Log.e("MainOnActivityResultErr", "error")
        }
    }


    fun displayBoards(boardsList: ArrayList<Board>){

        mainContentBinding = appBarMainBinding.mainContentIncluded;

        hideProgressDialog();

        if(boardsList.size > 0){
            mainContentBinding.rvBoards.visibility = View.VISIBLE;
            mainContentBinding.tvNoBoardsAvailable.visibility = View.GONE;
            mainContentBinding.tvTip.visibility = View.GONE;
            mainContentBinding.ivNoBoardsIllustration.visibility = View.GONE;

            mainContentBinding.rvBoards.layoutManager = LinearLayoutManager(this@MainActivity);
            mainContentBinding.rvBoards.setHasFixedSize(true);

            val adapter = BoardItemsAdapter(this@MainActivity, boardsList);
            mainContentBinding.rvBoards.adapter = adapter;

            adapter.setOnClickListener(object: BoardItemClickInterface {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID);
                    startActivity(intent);
                }

            })

            Log.i("displayBoards","Board adapter size: ${adapter.itemCount}");
            adapter.notifyDataSetChanged();

        }

        else{
            mainContentBinding.rvBoards.visibility = View.GONE;
            mainContentBinding.tvNoBoardsAvailable.visibility = View.VISIBLE;
            mainContentBinding.tvTip.visibility = View.VISIBLE;
            mainContentBinding.ivNoBoardsIllustration.visibility = View.VISIBLE;
        }
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11;
        const val CREATE_BOARD_REQUEST_CODE: Int = 12;
    }

}
