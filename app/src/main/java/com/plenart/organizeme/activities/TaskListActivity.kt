package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.TaskListItemsAdapter
import com.plenart.organizeme.databinding.ActivityTaskListBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var activityTaskListBinding: ActivityTaskListBinding
    private lateinit var mBoardDetails: Board;
    private lateinit var mBoardDocumentID: String;
    lateinit var mAssignedMemberDetailList: ArrayList<User>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTaskListBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(activityTaskListBinding.root)

        var boardDocumentID = "";
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }


        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().getBoardDetails(this,mBoardDocumentID);

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

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().getAssignedMembersListDetails(this, mBoardDetails.assignedTo);

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog();
        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().getBoardDetails(this, mBoardDetails.documentID);

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait));
            Firestore().getBoardDetails(this, mBoardDocumentID);
        }
        else{
            Log.e("cancelled","cancelled")
        }

    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, Firestore().getCurrentUserID());
        mBoardDetails.taskList.add(0,task);
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().addUpdateTaskList(this,mBoardDetails);

    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy);
        mBoardDetails.taskList[position] = task;
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().addUpdateTaskList(this,mBoardDetails);
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position);

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().addUpdateTaskList(this,mBoardDetails);
    }

    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        val cardAssignedUsersList: ArrayList<String> = ArrayList();
        cardAssignedUsersList.add(Firestore().getCurrentUserID());

        val card = Card(cardName, Firestore().getCurrentUserID(), cardAssignedUsersList);

        val cardsList = mBoardDetails.taskList[position].cards;
        cardsList.add(card);

        val task = Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].title,
            cardsList);

        mBoardDetails.taskList[position] = task;

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().addUpdateTaskList(this,mBoardDetails);

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
                startActivityForResult(intent, MEMBERS_REQUEST_CODE);
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){

        intent = Intent(this, CardDetailsActivity::class.java);
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails);
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition);
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition);
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList);

        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE);
    }

    fun boardMembersDetailsList(list: ArrayList<User>){
        mAssignedMemberDetailList = list;
        hideProgressDialog();

        val addTaskList = Task(resources.getString(R.string.add_list));
        mBoardDetails.taskList.add(addTaskList);

        activityTaskListBinding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        activityTaskListBinding.rvTaskList.setHasFixedSize(true);

        val adapter = TaskListItemsAdapter(this,mBoardDetails.taskList);
        activityTaskListBinding.rvTaskList.adapter = adapter;
    }

    fun updateCardsInTaskList(position: Int, cards: ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        mBoardDetails.taskList[position].cards = cards;
        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().addUpdateTaskList(this,mBoardDetails);

    }

    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13;
        const val CARD_DETAILS_REQUEST_CODE: Int = 14;
    }

}