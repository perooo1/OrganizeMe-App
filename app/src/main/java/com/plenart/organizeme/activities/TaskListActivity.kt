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
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.TaskListViewModel
import kotlinx.coroutines.launch

class TaskListActivity : BaseActivity() {

    private lateinit var activityTaskListBinding: ActivityTaskListBinding
    lateinit var viewModel: TaskListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityTaskListBinding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(activityTaskListBinding.root)

        Log.i("TaskListActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(TaskListViewModel::class.java)

        initObservers()

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            Log.i("onCreateTaskList","board document ID before: ${viewModel.boardDocumentID.value.toString()}")
            viewModel.setBoardDocumentID(intent.getStringExtra(Constants.DOCUMENT_ID)!!)
            Log.i("onCreateTaskList","board document ID after: ${viewModel.boardDocumentID.value.toString()}")
        }

        lifecycleScope.launch {
            viewModel.getBoardDetails()
        }

    }

    private fun initObservers() {
        assignedMembersObserver()
        taskAddedUpdatedObserver()
        boardDocumentIDObserver()
        boardDetailsObserver()
    }

    private fun boardDetailsObserver() {
        Log.i("boardDetailsObserver","boardDetails in viewmodel object ${viewModel.boardDetails?.value.toString()}")

        var isNull = true;
        viewModel.boardDetails?.observe(this, Observer { newBoard ->
            if(newBoard != null){
                Log.i("boardDetailsObserver","after setUserdataInUi : ${newBoard.toString()}")
                setUpActionBar()
                lifecycleScope.launch {
                    viewModel.getAssignedMembersListDetails()
                }
            }
            else{
                isNull = viewModel.checkBoardDetails()
                if(isNull){
                    Log.i("boardDetailsObserver","the else block")
                }
                else{
                    if (newBoard != null) {
                        setUpActionBar()
                        lifecycleScope.launch {
                            viewModel.getAssignedMembersListDetails()
                        }
                    };
                }
            }
        } )
    }

    private fun boardDocumentIDObserver() {
        viewModel.boardDocumentID.observe(this, Observer {
            if(it.isNullOrEmpty()){
                viewModel.setBoardDocumentID(intent.getStringExtra(Constants.DOCUMENT_ID)!!)
            }
            else{
                Log.i("boardDocumentIDObserver","it != null || empty")
            }
        })
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
                    Log.i("assignedMembersObserver","assignedMembers is empty or null! ${viewModel.assignedMemberDetailList.value.toString()}")
                }
                else{
                    boardMembersDetailsListNEW()
                }
            }
        })
    }


    private fun setUpActionBar(){
        setSupportActionBar(activityTaskListBinding.toolbarTaskListActivity)

        activityTaskListBinding.toolbarTaskListActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = viewModel.boardDetails?.value?.name
        }
        activityTaskListBinding.toolbarTaskListActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    fun addUpdateTaskListSuccess(){
        lifecycleScope.launch {
            viewModel.getBoardDetails()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE){
                lifecycleScope.launch {
                    viewModel.firestore.getBoardDetailsNEW(viewModel.boardDocumentID.value.toString())
                }
        }
        else{
            Log.e("cancelled","cancelled")
        }
    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, viewModel.firestore.getCurrentUserID())
        viewModel.boardDetails?.value?.taskList?.add(0,task)
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)
        viewModel.boardDetails?.value?.taskList?.set(position, task)
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    fun deleteTaskList(position: Int){
        viewModel.boardDetails?.value?.taskList?.removeAt(position)
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    fun addCardToTaskList(position: Int, cardName: String){
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(viewModel.firestore.getCurrentUserID())

        val card = Card(cardName, viewModel.firestore.getCurrentUserID(), cardAssignedUsersList)

        val cardsList = viewModel.boardDetails?.value?.taskList?.get(position)?.cards
        cardsList?.add(card)

        val task = Task(viewModel.boardDetails?.value?.taskList?.get(position)?.title.toString(),
            viewModel.boardDetails?.value?.taskList?.get(position)?.createdBy.toString(),
            cardsList!!
        )

        viewModel.boardDetails?.value?.taskList?.set(position, task)
        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, viewModel.boardDetails?.value)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){

        intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, viewModel.boardDetails?.value)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,viewModel.assignedMemberDetailList.value)

        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardMembersDetailsListNEW(){
        val addTaskList = Task(resources.getString(R.string.add_list))
        viewModel.boardDetails?.value?.taskList?.add(addTaskList)

        activityTaskListBinding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityTaskListBinding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this,viewModel.boardDetails?.value?.taskList!!)
        activityTaskListBinding.rvTaskList.adapter = adapter
    }

    fun updateCardsInTaskList(position: Int, cards: ArrayList<Card>){
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))
        viewModel.boardDetails?.value?.taskList?.get(position)!!.cards = cards

        viewModel.firestore.addUpdateTaskListNEW(viewModel.boardDetails?.value!!)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }

}