package com.plenart.organizeme.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.TaskListItemsAdapter
import com.plenart.organizeme.databinding.FragmentTaskListBinding
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.TaskListViewModel


class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding
    val viewModel: TaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTaskListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        initObservers()
        getArgs()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_members,menu)
        //super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getArgs(){
        val args: TaskListFragmentArgs by navArgs()
        viewModel.getBoardDetails(args.documentId)
    }

    private fun initObservers() {
        initAssignedMembers()
        initTaskAddedUpdated()
        initBoardDetails()
    }

    private fun initBoardDetails() {
        viewModel.boardDetails?.observe(viewLifecycleOwner, Observer { newBoard ->
            if(newBoard != null){
                viewModel.getAssignedMembersListDetails()
            }
            else{
                Log.i("boardDetailsObserver","error observing boardDetails ")
            }
        } )
    }

    private fun initTaskAddedUpdated() {
        viewModel.taskAddedUpdated.observe(viewLifecycleOwner, Observer {
            if(it){
                addUpdateTaskListSuccess()
            }
            else{
                Log.i("taskAddedUpdatedObserver","task addedUpdate failed: it==false")
            }
        })
    }

    private fun initAssignedMembers() {
        viewModel.assignedMemberDetailList.observe(viewLifecycleOwner, Observer { members ->
            if(members != null && members.isNotEmpty()){
                boardMembersDetailsList()
            }
            else{
                Log.i("assignedMembersObserver","assignedMembers is empty or null! ${viewModel.assignedMemberDetailList.value.toString()}")
            }
        })
    }

    private fun addUpdateTaskListSuccess(){
        val data = requireArguments()
        viewModel.getBoardDetails(data.getString(Constants.DOCUMENT_ID).toString())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE){

            //viewModel.getBoardDetails(intent.getStringExtra(Constants.DOCUMENT_ID)!!)

        }
        else{
            Log.e("cancelled","cancelled")
        }
    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, viewModel.firestore.getCurrentUserID())
        viewModel.boardDetails?.value?.taskList?.add(0,task)
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        viewModel.firestore.addUpdateTaskList(viewModel.boardDetails?.value!!)
    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)
        viewModel.boardDetails?.value?.taskList?.set(position, task)
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        viewModel.firestore.addUpdateTaskList(viewModel.boardDetails?.value!!)
    }

    fun deleteTaskList(position: Int){
        viewModel.boardDetails?.value?.taskList?.removeAt(position)
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))

        viewModel.firestore.addUpdateTaskList(viewModel.boardDetails?.value!!)
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
        viewModel.firestore.addUpdateTaskList(viewModel.boardDetails?.value!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val directions = TaskListFragmentDirections.actionTaskListFragmentToMembersFragment(
            viewModel.boardDetails?.value!!
        )
        findNavController().navigate(directions)
        return true
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){
        val directions = TaskListFragmentDirections.actionTaskListFragmentToCardDetailsFragment(
            taskListPosition,
            cardPosition,
            viewModel.boardDetails?.value!!,
            (viewModel.assignedMemberDetailList.value)?.toTypedArray()!!
        )
        findNavController().navigate(directions)

    }

    private fun boardMembersDetailsList(){
        val addTaskList = Task(resources.getString(R.string.add_list))
        viewModel.boardDetails?.value?.taskList?.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(requireActivity(),viewModel.boardDetails?.value?.taskList!!,this)
        binding.rvTaskList.adapter = adapter
    }

    fun updateCardsInTaskList(position: Int, cards: ArrayList<Card>){
        viewModel.boardDetails?.value?.taskList?.removeAt(viewModel.boardDetails?.value?.taskList?.size!!.minus(1))
        viewModel.boardDetails?.value?.taskList?.get(position)!!.cards = cards

        viewModel.firestore.addUpdateTaskList(viewModel.boardDetails?.value!!)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }

}