package com.plenart.organizeme.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.MemberListItemAdapter
import com.plenart.organizeme.databinding.ActivityMembersBinding
import com.plenart.organizeme.databinding.DialogAddSearchMemberBinding
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.MembersViewModel
import kotlinx.coroutines.launch

class MembersActivity : BaseActivity() {
    private lateinit var activityMembersBinding: ActivityMembersBinding
    private lateinit var viewModel: MembersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMembersBinding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(activityMembersBinding.root)

        Log.i("MembersActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(MembersViewModel::class.java)

        setUpActionBar()
        initObservers()

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            viewModel.setBoardDetails(intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!)
        }

        lifecycleScope.launch {
            viewModel.getAssignedMembersListDetails()
        }

        activityMembersBinding.fabMember.setOnClickListener {
            Log.e("heh","fab click click clickity click")
            dialogAddSearchMember();
        }
    }

    private fun initObservers() {
        initAssignedMembers()
        initMemberAssigned()
        initMember()
    }

    private fun initMember() {
        viewModel.member?.observe(this, Observer{
            memberDetailsNEW()
        })
    }

    private fun initMemberAssigned() {
        viewModel.memberAssignSuccess.observe(this, Observer {
            if (it){
                memberAssignSuccessNEW()
            }
            else{
                Log.i("memberAssignedObserver","error assigning member; it == false")
            }
        })
    }

    private fun initAssignedMembers() {
        var isNull = true
        viewModel.assignedMemberDetailList.observe(this, Observer { members ->
            if(members != null && members.isNotEmpty()){
                setUpMembersListNEW()
                Log.i("assignedMembersObserverMembers","assignedMembersObserver function triggered - first if call")
            }
            else{
                isNull = viewModel.checkAssignedMembers()
                if(isNull){
                    Toast.makeText(this, "assignedMembers is empty or null!", Toast.LENGTH_SHORT).show()
                    Log.i("assignedMembersObserverMembers","assignedMembers is empty or null! ${viewModel.assignedMemberDetailList.value.toString()}")
                }
                else{
                    setUpMembersListNEW()
                }
            }
        })
    }

    private fun setUpActionBar(){
        setSupportActionBar(activityMembersBinding.toolbarMembersActivity)

        activityMembersBinding.toolbarMembersActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = resources.getString(R.string.members);
        }

        activityMembersBinding.toolbarMembersActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    override fun onBackPressed() {
        if(viewModel.anyChangesMade.value == true){
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed()
    }

    private fun setUpMembersListNEW(){
        activityMembersBinding.rvMembers.layoutManager = LinearLayoutManager(this)
        activityMembersBinding.rvMembers.setHasFixedSize(true)

        val adapter = MemberListItemAdapter(this, viewModel.assignedMemberDetailList?.value!!)
        activityMembersBinding.rvMembers.adapter = adapter
    }

    private fun memberDetailsNEW(){
        viewModel.boardDetails?.value?.assignedTo?.add(viewModel.member?.value?.id.toString())
        viewModel.firestore.assignMemberToBoard(viewModel.boardDetails?.value!!)
    }

    private fun memberAssignSuccessNEW(){
        viewModel.assignedMemberDetailList.value?.add(viewModel.member?.value!!)
        viewModel.setAnyChangesMade(true)
        setUpMembersListNEW()
    }

    private fun dialogAddSearchMember(){

        val dialog = Dialog(this)

        var dialogBinding: DialogAddSearchMemberBinding = DialogAddSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvAddMember.setOnClickListener {
            viewModel.setEmail(dialogBinding.etEmailSearchMember.text.toString())
            if(viewModel.email.value?.isNotEmpty() == true){
                dialog.dismiss();
                lifecycleScope.launchWhenCreated {
                    viewModel.setMember(viewModel.firestore.getMemberDetails(viewModel.email.value!!))
                }
            }
            else{
                Toast.makeText(this, "Please enter members' email address",Toast.LENGTH_SHORT).show()
            }
        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss();
        }
        dialog.show()
    }

}