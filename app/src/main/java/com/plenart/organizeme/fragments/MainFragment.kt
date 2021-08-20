package com.plenart.organizeme.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.databinding.FragmentMainBinding
import com.plenart.organizeme.databinding.MainContentBinding
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.MainActivityViewModel


class MainFragment : Fragment() {
    private lateinit var fragmentMainBinding: FragmentMainBinding
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mainContentBinding: MainContentBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainBinding = FragmentMainBinding.inflate(inflater,container,false)
        val view = fragmentMainBinding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initObservers()
        initListeners()

        viewModel.loadUserData()
    }

    private fun initListeners() {

        fragmentMainBinding.fabCreateBoard.setOnClickListener{

            val fragment = CreateBoardFragment()
            val bundle = Bundle()
            bundle.putString(Constants.NAME,viewModel.userName.value)
            fragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_main,fragment)
                .commit()

        }

    }

    private fun initObservers() {
        initUser()
        initBoardsList()
    }

    private fun initBoardsList() {
        viewModel.boardsList.observe(viewLifecycleOwner, Observer { boardsList ->
            if(boardsList != null && boardsList.isNotEmpty()){
                displayBoards(boardsList);
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
        if(activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            activityMainBinding.drawerLayout.openDrawer(GravityCompat.START)
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
        if(activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            //doubleBackToExit()
        }
    }

    private fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User, readBoardsList: Boolean = false) {
        //actual implementation of this function is currently in MainActivity
        viewModel.getBoardsList()
    }

    private fun displayBoards(boardsList: ArrayList<Board>){
        mainContentBinding = fragmentMainBinding.mainContentIncluded
        Log.i("displayBoards","functin triggered")

        if(boardsList.size > 0){
            Log.i("displayBoards","inside if call ")
            mainContentBinding.rvBoards.visibility = View.VISIBLE
            mainContentBinding.tvNoBoardsAvailable.visibility = View.GONE
            mainContentBinding.tvTip.visibility = View.GONE
            mainContentBinding.ivNoBoardsIllustration.visibility = View.GONE

            mainContentBinding.rvBoards.layoutManager = LinearLayoutManager(context)
            mainContentBinding.rvBoards.setHasFixedSize(true)

            val adapter = context?.let { BoardItemsAdapter(it, boardsList) }
            Log.i("displayBoards","adapter is ${adapter.toString()}")
            mainContentBinding.rvBoards.adapter = adapter

            adapter?.setOnClickListener(object: BoardItemClickInterface {
                override fun onClick(position: Int, model: Board) {

                    val bundle = Bundle()
                    bundle.putString(Constants.DOCUMENT_ID,model.documentID)
                    val fragment = TaskListFragment()
                    fragment.arguments = bundle
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_main,fragment)
                        .commit()

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