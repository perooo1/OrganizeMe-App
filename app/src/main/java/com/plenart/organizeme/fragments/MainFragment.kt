package com.plenart.organizeme.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.ActivityMainBinding
import com.plenart.organizeme.databinding.FragmentMainBinding
import com.plenart.organizeme.databinding.MainContentBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.viewModels.MainActivityViewModel
import de.hdodenhof.circleimageview.CircleImageView


class MainFragment : Fragment() {
    private lateinit var fragmentMainBinding: FragmentMainBinding
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mainContentBinding: MainContentBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)
        return fragmentMainBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()

        viewModel.loadUserData()
    }

    private fun initListeners() {

        fragmentMainBinding.fabCreateBoard.setOnClickListener{
            val directions = MainFragmentDirections.actionMainFragmentToCreateBoardFragment(
                viewModel.userName.value.toString()
            )
            findNavController().navigate(directions)
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

    private fun updateNavigationUserDetails(loggedInUser: com.plenart.organizeme.models.User, readBoardsList: Boolean = false) {

        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val userImage = header.findViewById<CircleImageView>(R.id.nav_user_img)
        val userName = header.findViewById<TextView>(R.id.tv_username)

        Glide.with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userImage)

        userName.text = loggedInUser.name

        if(readBoardsList){
            viewModel.getBoardsList()
        }

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

                    val directions = MainFragmentDirections.actionMainFragmentToTaskListFragment(
                        model.documentID
                    )

                    findNavController().navigate(directions)

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