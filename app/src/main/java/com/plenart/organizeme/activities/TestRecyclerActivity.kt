package com.plenart.organizeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.BoardItemsAdapter
import com.plenart.organizeme.databinding.ActivityTestRecyclerBinding
import com.plenart.organizeme.databinding.MainContentBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.interfaces.BoardItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants

class TestRecyclerActivity : BaseActivity() {
    private lateinit var activityTestRecyclerBinding: ActivityTestRecyclerBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityTestRecyclerBinding = ActivityTestRecyclerBinding.inflate(layoutInflater);
        setContentView(activityTestRecyclerBinding.root)

        setUpActionBar();

        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().getBoardsList2(this);

    }

    private fun setUpActionBar(){
        setSupportActionBar(activityTestRecyclerBinding.toolbarTestRecyclerActivity)

        activityTestRecyclerBinding.toolbarTestRecyclerActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = resources.getString(R.string.nav_my_boards);
        }
        activityTestRecyclerBinding.toolbarTestRecyclerActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }


    fun displayRecyclerView(boardsList: ArrayList<Board>){

        hideProgressDialog();

        if(boardsList.size > 0){
            activityTestRecyclerBinding.rvBoards2.visibility = View.VISIBLE;
            activityTestRecyclerBinding.tvNoBoardsAvailable2.visibility = View.GONE;

            activityTestRecyclerBinding.rvBoards2.layoutManager = LinearLayoutManager(this@TestRecyclerActivity);
            activityTestRecyclerBinding.rvBoards2.setHasFixedSize(true);

            val adapter = BoardItemsAdapter(this@TestRecyclerActivity, boardsList);
            activityTestRecyclerBinding.rvBoards2.adapter = adapter;

            adapter.setOnClickListener(object: BoardItemClickInterface{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@TestRecyclerActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentID);
                    startActivity(intent);
                }

            })


            Log.i("POPUI","Board adapter size: ${adapter.itemCount}");
            adapter.notifyDataSetChanged();

        }

        else{
            activityTestRecyclerBinding.rvBoards2.visibility = View.GONE;
            activityTestRecyclerBinding.tvNoBoardsAvailable2.visibility = View.VISIBLE;
        }

    }


}