package com.plenart.organizeme.activities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.MemberListItemAdapter
import com.plenart.organizeme.databinding.ActivityMembersBinding
import com.plenart.organizeme.databinding.DialogAddSearchMemberBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var activityMembersBinding: ActivityMembersBinding;
    private lateinit var mBoardDetails: Board;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMembersBinding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(activityMembersBinding.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setUpActionBar();

        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assignedTo);

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

    fun setUpMembersList(list: ArrayList<User>){
        hideProgressDialog();

        activityMembersBinding.rvMembers.layoutManager = LinearLayoutManager(this);
        activityMembersBinding.rvMembers.setHasFixedSize(true);

        val adapter = MemberListItemAdapter(this, list);
        activityMembersBinding.rvMembers.adapter = adapter;

    }

    private fun dialogAddSearchMember(){

        val dialog = Dialog(this);

        var dialogBinding: DialogAddSearchMemberBinding = DialogAddSearchMemberBinding.inflate(layoutInflater);
        dialog.setContentView(dialogBinding.root);

        dialogBinding.tvAddMember.setOnClickListener {
            val email = dialogBinding.etEmailSearchMember.text.toString();

            if(email.isNotEmpty()){
                dialog.dismiss();
                //TODO implement logic
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



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                Log.e("sth","adddd member click click");
                dialogAddSearchMember();
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }



}