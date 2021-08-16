package com.plenart.organizeme.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.plenart.organizeme.R
import com.plenart.organizeme.activities.*
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.*
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.MainActivityViewModel
import de.hdodenhof.circleimageview.CircleImageView


class MainFragment : Fragment() {
    private lateinit var fragmentMainBinding: FragmentMainBinding
    private lateinit var activitySplashBinding: ActivitySplashBinding
    private lateinit var mainContentBinding: MainContentBinding
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainBinding = FragmentMainBinding.inflate(inflater,container,false)
        return fragmentMainBinding.root

        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initObservers()
        initListeners()

        viewModel.loadUserData()
    }

    private fun initListeners() {
        /*
        activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        activitySplashBinding.navView.setNavigationItemSelectedListener(this)
        TODO check later
         */

        //fragmentMainBinding.navView.setNavigationItemSelectedListener(this)

        fragmentMainBinding.fabCreateBoard.setOnClickListener{
            FirebaseAuth.getInstance().signOut()                        //for testing purposes only
            val fragment = IntroFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_main, fragment)
                .commit()
        /*
            val intent = Intent(activity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, viewModel.userName.value)

            startActivityForResult(intent, MainActivity.CREATE_BOARD_REQUEST_CODE)
        */
        }

    }

    private fun initObservers() {
        initUser()
        initBoardsList()
    }

    private fun initBoardsList() {
        Log.i("displayBoards","obards list observer!")
        viewModel.boardsList.observe(viewLifecycleOwner, Observer { boardsList ->
            Log.i("displayBoards","obards list observer inside observe block!")
            if(boardsList != null && boardsList.isNotEmpty()){
                Log.i("displayBoards","obards list observer inside if condition")
                Log.i("displayBoards","obards list observer inside if condition: boardslist is: ${boardsList.toString()}")
                displayBoards(boardsList);
                Log.i("displayBoards","obards list observer inside condition, after needed function call")
            }
            else{
                Log.i("boardsListObserver","boardListObserver: boardsList is empty or null!")
            }

        })
    }

    private fun initUser() {
        viewModel.user?.observe(viewLifecycleOwner, Observer { newUser ->
            if(newUser != null){
                updateNavigationUserDetails(newUser,true)
            }
            else{
                Log.i("UserObserver","user observer: user is null")
            }

        } )
    }

    private fun toggleDrawer(){
        if(activitySplashBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            activitySplashBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            activitySplashBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setUpActionBar(){
        /*
        //setSupportActionBar(appBarMainBinding.toolbarMainActivity)
        fragmentMainBinding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        fragmentMainBinding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
        */
    }

    fun onBackPressed() {
        if(activitySplashBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            activitySplashBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            //doubleBackToExit()
        }
    }

    private fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User, readBoardsList: Boolean = false) {
/*      TODO FIX NPE WITH requireView()
        val nav_user_img: CircleImageView = requireView().findViewById(R.id.nav_user_img)
        val tv_username: TextView = requireView().findViewById(R.id.tv_username)

        Glide.with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_img)

        tv_username.text = loggedInUser.name

        if(readBoardsList){
            //showProgressDialog(resources.getString(R.string.please_wait))

            viewModel.getBoardsList()

        }

 */
    }

    private fun displayBoards(boardsList: ArrayList<Board>){
        mainContentBinding = fragmentMainBinding.mainContentIncluded
        Log.i("displayBoards","functin triggered")
        //hideProgressDialog()

        if(boardsList.size > 0){
            Log.i("displayBoards","inside if call ")
            mainContentBinding.rvBoards.visibility = View.VISIBLE
            mainContentBinding.tvNoBoardsAvailable.visibility = View.GONE
            mainContentBinding.tvTip.visibility = View.GONE
            mainContentBinding.ivNoBoardsIllustration.visibility = View.GONE

            mainContentBinding.rvBoards.layoutManager = LinearLayoutManager(context)       //was context
            mainContentBinding.rvBoards.setHasFixedSize(true)

            val adapter = context?.let { BoardItemsAdapter(it, boardsList) }
            //val adapter = BoardItemsAdapter(context, boardsList)                  //backup for line above
            Log.i("displayBoards","adapter is ${adapter.toString()}")
            mainContentBinding.rvBoards.adapter = adapter

            adapter?.setOnClickListener(object: BoardItemClickInterface {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(activity, TaskListActivity::class.java)        //either pass activity or context as first param
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID)
                    startActivity(intent)
                }
            })

            adapter?.notifyDataSetChanged()
        }
        else{
            mainContentBinding.rvBoards.visibility = View.GONE
            mainContentBinding.tvNoBoardsAvailable.visibility = View.VISIBLE
            mainContentBinding.tvTip.visibility = View.VISIBLE
            mainContentBinding.ivNoBoardsIllustration.visibility = View.VISIBLE
        }
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}