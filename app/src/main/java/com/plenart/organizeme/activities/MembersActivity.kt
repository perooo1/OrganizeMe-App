package com.plenart.organizeme.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.MemberListItemAdapter
import com.plenart.organizeme.databinding.ActivityMembersBinding
import com.plenart.organizeme.databinding.DialogAddSearchMemberBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var activityMembersBinding: ActivityMembersBinding;
    private lateinit var mBoardDetails: Board;

    private lateinit var mAssignedMembersList: ArrayList<User>;

    private var anyChangesMade: Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMembersBinding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(activityMembersBinding.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setUpActionBar();

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().getAssignedMembersListDetails(this,mBoardDetails.assignedTo);

        activityMembersBinding.fabMember.setOnClickListener {
            Log.e("heh","fab click click clickity click");
            dialogAddSearchMember();
        }
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
        if(anyChangesMade){
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed()
    }

    fun setUpMembersList(list: ArrayList<User>){

        mAssignedMembersList = list;
        hideProgressDialog();

        activityMembersBinding.rvMembers.layoutManager = LinearLayoutManager(this);
        activityMembersBinding.rvMembers.setHasFixedSize(true);

        val adapter = MemberListItemAdapter(this, list);
        activityMembersBinding.rvMembers.adapter = adapter;

    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id);
        Firestore().assignMemberToBoard(this,mBoardDetails,user);

    }

    fun memberAssignSuccess(user: User){
        hideProgressDialog();
        mAssignedMembersList.add(user);
        anyChangesMade = true;
        setUpMembersList(mAssignedMembersList);

    }

    private fun dialogAddSearchMember(){

        val dialog = Dialog(this);

        var dialogBinding: DialogAddSearchMemberBinding = DialogAddSearchMemberBinding.inflate(layoutInflater);
        dialog.setContentView(dialogBinding.root);

        dialogBinding.tvAddMember.setOnClickListener {
            val email = dialogBinding.etEmailSearchMember.text.toString();

            if(email.isNotEmpty()){
                dialog.dismiss();
                showProgressDialog(resources.getString(R.string.please_wait));

                Firestore().getMemberDetails(this, email);

            }
            else{
                Toast.makeText(this, "Please enter members' email address",Toast.LENGTH_SHORT).show();
            }
        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss();
        }

        dialog.show();

    }

}