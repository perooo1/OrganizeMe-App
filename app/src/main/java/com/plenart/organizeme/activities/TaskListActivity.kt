package com.plenart.organizeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityTaskListBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var activityTaskListBinding: ActivityTaskListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTaskListBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(activityTaskListBinding.root)

        var boardDocumentID = "";
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }


        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().getBoardDetails(this,boardDocumentID);

    }


    private fun setUpActionBar(title: String){
        setSupportActionBar(activityTaskListBinding.toolbarTaskListActivity)

        activityTaskListBinding.toolbarTaskListActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = title;
        }
        activityTaskListBinding.toolbarTaskListActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    fun boardDetails(board:Board){
        hideProgressDialog();
        setUpActionBar(board.name);



    }

}