package com.plenart.organizeme.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.FragmentMainBinding
import com.plenart.organizeme.databinding.MainContentBinding
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.gone
import com.plenart.organizeme.utils.loadImage
import com.plenart.organizeme.utils.visible
import com.plenart.organizeme.viewModels.MainActivityViewModel
import de.hdodenhof.circleimageview.CircleImageView


class MainFragment : Fragment() {
    private lateinit var fragmentMainBinding: FragmentMainBinding
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
        fragmentMainBinding.fabCreateBoard.setOnClickListener {
            val directions =
                DrawerHostFragmentDirections.actionSecNavHostFragmentToCreateBoardFragment(
                    viewModel.userName
                )
            requireActivity().findNavController(R.id.main_content_navigation_component)
                .navigate(directions)

        }

    }

    private fun initObservers() {
        initUser()
        initBoardsList()
    }

    private fun initBoardsList() {
        viewModel.boardsList.observe(viewLifecycleOwner, Observer { boardsList ->
            if (boardsList != null && boardsList.isNotEmpty()) {
                displayBoards(boardsList)
            } else {
                Log.i("boardsListObserver", "boardListObserver: boardsList is empty or null!")
            }

        })
    }

    private fun initUser() {
        viewModel.user.observe(viewLifecycleOwner, Observer { newUser ->
            if (newUser != null) {
                updateNavigationUserDetails(newUser, true)
            } else {
                Log.i("UserObserver", "user observer: user is null")
            }

        })
    }

    private fun updateNavigationUserDetails(
        loggedInUser: com.plenart.organizeme.models.User,
        readBoardsList: Boolean = false
    ) {

        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val userImage = header.findViewById<CircleImageView>(R.id.nav_user_img)
        val userName = header.findViewById<TextView>(R.id.tv_username)

        userImage.loadImage(loggedInUser.image)
        userName.text = loggedInUser.name

        if (readBoardsList) {
            viewModel.getBoardsList()
        }

    }

    private fun displayBoards(boardsList: ArrayList<Board>) {
        mainContentBinding = fragmentMainBinding.mainContentIncluded
        configureAdapter()

        if (boardsList.size > 0) {
            mainContentBinding.apply {
                rvBoards.visible()
                tvNoBoardsAvailable.gone()
                tvTip.gone()
                ivNoBoardsIllustration.gone()

            }
        } else {
            mainContentBinding.apply {
                rvBoards.gone()
                tvNoBoardsAvailable.visible()
                tvTip.visible()
                ivNoBoardsIllustration.visible()
            }
        }
    }

    private fun configureAdapter() {
        val adapterToSet = BoardItemsAdapter()

        viewModel.boardsList.observe(viewLifecycleOwner, Observer {
            adapterToSet.submitList(it)
        })

        mainContentBinding.rvBoards.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = adapterToSet
        }

        adapterToSet.setOnClickListener(object : BoardItemClickInterface {
            override fun onClick(position: Int, model: Board) {

                val directions =
                    DrawerHostFragmentDirections.actionSecNavHostFragmentToTaskListFragment(
                        model.documentID
                    )
                requireActivity().findNavController(R.id.main_content_navigation_component)
                    .navigate(directions)
            }
        })

    }

}