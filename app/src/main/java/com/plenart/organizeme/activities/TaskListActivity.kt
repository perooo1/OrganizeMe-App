package com.plenart.organizeme.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.TaskListItemsAdapter
import com.plenart.organizeme.databinding.ActivityTaskListBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var activityTaskListBinding: ActivityTaskListBinding
    private lateinit var mBoardDetails: Board;

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


    private fun setUpActionBar(){
        setSupportActionBar(activityTaskListBinding.toolbarTaskListActivity)

        activityTaskListBinding.toolbarTaskListActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = mBoardDetails.name;
        }
        activityTaskListBinding.toolbarTaskListActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    fun boardDetails(board:Board){

        mBoardDetails = board;

        hideProgressDialog();
        setUpActionBar();

        val addTaskList = Task(resources.getString(R.string.add_list));
        board.taskList.add(addTaskList);

        activityTaskListBinding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        activityTaskListBinding.rvTaskList.setHasFixedSize(true);

        val adapter = TaskListItemsAdapter(this,board.taskList);
        activityTaskListBinding.rvTaskList.adapter = adapter;
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog();
        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentID);

    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, FirestoreClass().getCurrentUserID());
        mBoardDetails.taskList.add(0,task);
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().addUpdateTaskList(this,mBoardDetails);

    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy);
        mBoardDetails.taskList[position] = task;
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().addUpdateTaskList(this,mBoardDetails);
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position);

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().addUpdateTaskList(this,mBoardDetails);
    }

    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        val cardAssignedUsersList: ArrayList<String> = ArrayList();
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserID());

        val card = Card(cardName, FirestoreClass().getCurrentUserID(), cardAssignedUsersList);

        val cardsList = mBoardDetails.taskList[position].cards;
        cardsList.add(card);

        val task = Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].title,
            cardsList);

        mBoardDetails.taskList[position] = task;

        showProgressDialog(resources.getString(R.string.please_wait));
        FirestoreClass().addUpdateTaskList(this,mBoardDetails);

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this, MembersActivity::class.java);
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item)
    }

}