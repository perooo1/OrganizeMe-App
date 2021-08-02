package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.plenart.organizeme.viewModels.TaskListViewModel
import kotlinx.coroutines.launch

class TaskListActivity : BaseActivity() {

    private lateinit var activityTaskListBinding: ActivityTaskListBinding
    private lateinit var viewModel: TaskListViewModel
    //private lateinit var mBoardDetails: Board;
    private lateinit var mBoardDocumentID: String;
    lateinit var mAssignedMemberDetailList: ArrayList<User>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTaskListBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(activityTaskListBinding.root)

        Log.i("TaskListActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(TaskListViewModel::class.java)

        initObservers()

        var boardDocumentID = "";
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            //mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID)!!
            viewModel.setBoardDocumentID(intent.getStringExtra(Constants.DOCUMENT_ID)!!)
        }


        //showProgressDialog(resources.getString(R.string.please_wait));
        //Firestore().getBoardDetails(this,mBoardDocumentID);

        //viewModel.firestore.getBoardDetailsNEW(viewModel.boardDocumentID.value!!)         //careful about !!
        //viewModel.getBoardDetails()                                                       // no need, called from viewModelScope corutine
        setUpActionBar2()

    }

    private fun initObservers() {
        assignedMembersObserver()
        taskAddedUpdatedObserver()
    }

    private fun taskAddedUpdatedObserver() {
        viewModel.taskAddedUpdated.observe(this, Observer {
            if(it){
                addUpdateTaskListSuccess()
            }
            else{
                Log.i("taskAddedUpdatedObserver","task addedUpdate failed: it==false")
            }
        })
    }

    private fun assignedMembersObserver() {
        var isNull = true;
        viewModel.assignedMemberDetailList.observe(this, Observer { members ->
            if(members != null && members.isNotEmpty()){
                boardMembersDetailsListNEW()
                Log.i("assignedMembersObserver","assignedMembersObserver function triggered - first if call")
            }
            else{
                isNull = viewModel.checkAssignedMembers()
                if(isNull){
                    Toast.makeText(this, "assignedMembers is empty or null!", Toast.LENGTH_SHORT).show()
                    Log.i("assignedMembersObserver","assignedMembers is empty or null!");
                }
                else{
                    boardMembersDetailsListNEW()
                }
            }
        })
    }

/*
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
*/

    private fun setUpActionBar2(){
        setSupportActionBar(activityTaskListBinding.toolbarTaskListActivity)

        activityTaskListBinding.toolbarTaskListActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = viewModel.boardDetails?.value?.name;
        }
        activityTaskListBinding.toolbarTaskListActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }
/*
    fun boardDetails(board:Board){

        mBoardDetails = board;

        hideProgressDialog();
        setUpActionBar();

        showProgressDialog(resources.getString(R.string.please_wait));
        Firestore().getAssignedMembersListDetails(this, mBoardDetails.assignedTo);

    }
*/
    fun addUpdateTaskListSuccess(){
        hideProgressDialog();
        showProgressDialog(resources.getString(R.string.please_wait));
        //Firestore().getBoardDetails(this, mBoardDetails.documentID);

        lifecycleScope.launch {
            viewModel.getBoardDetails()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE){
            //showProgressDialog(resources.getString(R.string.please_wait));
            //Firestore().getBoardDetails(this, mBoardDocumentID);
                lifecycleScope.launch {
                    viewModel.firestore.getBoardDetailsNEW(viewModel.boardDocumentID.value.toString())
                }

        }
        else{
            Log.e("cancelled","cancelled")
        }

    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, viewModel.firestore.getCurrentUserID());
        viewModel.boardDetails?.value?.taskList?.add(0,task);
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))     //careful with null operators!!

        //mBoardDetails.taskList.add(0,task);
        //mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        //Firestore().addUpdateTaskList(this,mBoardDetails);
        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)               //careful with null operators!!

    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy);
        //viewModel.boardDetails.value.taskList[position] = task;
        viewModel.boardDetails?.value?.taskList?.set(position, task);
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))            //careful with null operators!!

        //mBoardDetails.taskList[position] = task;
        //mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        //showProgressDialog(resources.getString(R.string.please_wait));
        //Firestore().addUpdateTaskList(this,mBoardDetails);
        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)               //careful with null operators!!
    }

    fun deleteTaskList(position: Int){
        viewModel.boardDetails?.value?.taskList?.removeAt(position)
        //mBoardDetails.taskList.removeAt(position);

        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))
        //mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        showProgressDialog(resources.getString(R.string.please_wait));
        //Firestore().addUpdateTaskList(this,mBoardDetails);
        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    fun addCardToTaskList(position: Int, cardName: String){
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))
        //mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        val cardAssignedUsersList: ArrayList<String> = ArrayList();
        cardAssignedUsersList.add(viewModel.firestore.getCurrentUserID());

        val card = Card(cardName, viewModel.firestore.getCurrentUserID(), cardAssignedUsersList);

        val cardsList = viewModel.boardDetails?.value?.taskList?.get(position)?.cards
        cardsList?.add(card);

        val task = Task(viewModel.boardDetails?.value?.taskList?.get(position)?.title.toString(),
            viewModel.boardDetails?.value?.taskList?.get(position)?.createdBy.toString(),
            cardsList!!

        )

/*
        val task = Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].title,
            cardsList);
*/

        viewModel.boardDetails?.value?.taskList?.set(position, task);
        //mBoardDetails.taskList[position] = task;

        //showProgressDialog(resources.getString(R.string.please_wait));
        //Firestore().addUpdateTaskList(this,mBoardDetails);
        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this, MembersActivity::class.java);
                intent.putExtra(Constants.BOARD_DETAIL, viewModel.boardDetails?.value);
                startActivityForResult(intent, MEMBERS_REQUEST_CODE);
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){

        intent = Intent(this, CardDetailsActivity::class.java);
        intent.putExtra(Constants.BOARD_DETAIL, viewModel.boardDetails?.value);
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition);
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition);
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList);

        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE);
    }
/*
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
*/
    fun boardMembersDetailsListNEW(){
        hideProgressDialog();

        val addTaskList = Task(resources.getString(R.string.add_list));
        viewModel.boardDetails?.value?.taskList?.add(addTaskList);

        //mBoardDetails.taskList.add(addTaskList);

        activityTaskListBinding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        activityTaskListBinding.rvTaskList.setHasFixedSize(true);

        val adapter = TaskListItemsAdapter(this,viewModel.boardDetails?.value?.taskList!!);
        activityTaskListBinding.rvTaskList.adapter = adapter;
    }

    fun updateCardsInTaskList(position: Int, cards: ArrayList<Card>){
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))
        //mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1);

        viewModel.boardDetails?.value?.taskList?.get(position)!!.cards = cards;                                 //careful with null operators!!
        //mBoardDetails.taskList[position].cards = cards;

        //showProgressDialog(resources.getString(R.string.please_wait));
        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
        //Firestore().addUpdateTaskList(this,mBoardDetails);
    }

    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13;
        const val CARD_DETAILS_REQUEST_CODE: Int = 14;
    }

}