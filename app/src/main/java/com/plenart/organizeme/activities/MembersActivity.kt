package com.plenart.organizeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.MemberListItemAdapter
import com.plenart.organizeme.databinding.ActivityMembersBinding
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


}