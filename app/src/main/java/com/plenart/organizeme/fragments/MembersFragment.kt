package com.plenart.organizeme.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.MemberListItemAdapter
import com.plenart.organizeme.databinding.DialogAddSearchMemberBinding
import com.plenart.organizeme.databinding.FragmentMembersBinding
import com.plenart.organizeme.interfaces.SelectedMembersClickInterface
import com.plenart.organizeme.models.User
import com.plenart.organizeme.viewModels.MembersViewModel


class MembersFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    private val viewModel: MembersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initListeners()
        getArgs()

        viewModel.getAssignedMembersListDetails()

    }

    private fun getArgs() {
        val args: MembersFragmentArgs by navArgs()
        viewModel.setBoardDetails(args.boardDetails)

    }

    private fun initListeners() {
        binding.fabMember.setOnClickListener {
            dialogAddSearchMember();
        }
    }


    private fun initObservers() {
        initAssignedMembers()
        initMemberAssigned()
        initMember()
    }

    private fun initMember() {
        viewModel.member.observe(viewLifecycleOwner, Observer {
            memberDetails(it)
        })
    }

    private fun initMemberAssigned() {
        viewModel.memberAssignSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                memberAssignSuccess()
            } else {
                Log.i("memberAssignedObserver", "error assigning member; it == false")
            }
        })
    }

    private fun initAssignedMembers() {
        viewModel.assignedMemberDetailList.observe(viewLifecycleOwner, Observer { members ->
            if (members != null && members.isNotEmpty()) {
                setUpMembersList()
            } else {
                Log.i("assignedMembersObserverMembers", "assignedMembers is empty or null")
            }

        })
    }

    private fun setUpMembersList() {

        val adapterToSet =
            MemberListItemAdapter(viewModel.assignedMemberDetailList.value!!)
        binding.apply {
            rvMembers.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = adapterToSet

            }
        }
    }

    private fun memberDetails(user: User) {
        viewModel.apply {
            boardDetails.value?.assignedTo?.add(user.id)
            firestore.assignMemberToBoard(viewModel.boardDetails.value!!)
        }
    }

    private fun memberAssignSuccess() {
        viewModel.apply {
            assignedMemberDetailList.value?.add(viewModel.member.value!!)
            setAnyChangesMade(true)

        }
        setUpMembersList()
    }

    private fun dialogAddSearchMember() {

        val dialog = Dialog(requireContext())

        val dialogBinding: DialogAddSearchMemberBinding =
            DialogAddSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvAddMember.setOnClickListener {

            viewModel.apply {
                setEmail(dialogBinding.etEmailSearchMember.text.toString())
                if (viewModel.email.value?.isNotEmpty() == true) {
                    dialog.dismiss();
                    setMemberFromDialog()
                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.please_provide_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss();
        }
        dialog.show()
    }

}