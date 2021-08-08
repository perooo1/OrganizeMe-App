package com.plenart.organizeme.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.CardMembersListItemAdapter
import com.plenart.organizeme.databinding.ActivityCardDetailsBinding
import com.plenart.organizeme.dialogs.LabelColorListDialog
import com.plenart.organizeme.dialogs.MembersListDialog
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.SelectedMembers
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.CardDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var activityCardDetailsBinding: ActivityCardDetailsBinding;
    private lateinit var viewModel: CardDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCardDetailsBinding = ActivityCardDetailsBinding.inflate(layoutInflater);
        setContentView(activityCardDetailsBinding.root);

        Log.i("CardDetailsActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(CardDetailsViewModel::class.java)

        getIntentData()
        setUpActionBar()
        initObservers()

        setUpCardNameEt()
        setUpSelectedColor()

        getCardName()

        activityCardDetailsBinding.btnUpdateCardDetails.setOnClickListener {
            if(activityCardDetailsBinding.etNameCardDetails.text.toString().isNotEmpty()){
                viewModel.updateCardDetails()
            }
            else{
                Toast.makeText(this,"Please enter a card name", Toast.LENGTH_SHORT).show()
            }
        }

        activityCardDetailsBinding.tvSelectLabelColor.setOnClickListener {
            labelColorListDialog();
        }

        activityCardDetailsBinding.tvSelectMembers.setOnClickListener {
            membersListDialog();
        }

        viewModel.setSelectedDueDate(viewModel.boardDetails?.value?.taskList?.get(viewModel.taskListPosition.value!!)?.cards!![viewModel.cardPosition.value!!].dueDate)

        if(viewModel.selectedDueDateMilis.value!! > 0 ){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(viewModel.selectedDueDateMilis.value!!))
            activityCardDetailsBinding.tvSelectDueDate.text = selectedDate

        }

        activityCardDetailsBinding.tvSelectDueDate.setOnClickListener {
            showDatePicker();
        }

    }

    private fun getCardName() {
        activityCardDetailsBinding.etNameCardDetails.doAfterTextChanged {
            viewModel.setCardName(it.toString())
        }
    }

    private fun setUpSelectedColor() {
        viewModel.setSelectedColor(viewModel.boardDetails?.value?.taskList?.get(viewModel.taskListPosition.value!!)?.cards!![viewModel.cardPosition.value!!].labelColor)
        if(viewModel.selectedColor.value?.isNotEmpty() == true){
            setColor()
        }
    }

    private fun initObservers() {
        assignedMembersObserver()
        taskListUpdatedObserver()
        cardNameObserver()
    }

    private fun cardNameObserver() {
        viewModel.cardName.observe(this, androidx.lifecycle.Observer {
            if(it == null ){
                showErrorSnackBar("Please enter a card name")
            }
            else{
            Log.i("cardNameObserver","log log")
            }
        })
    }

    private fun taskListUpdatedObserver() {
        viewModel.taskListUpdated.observe(this, androidx.lifecycle.Observer {
            if(it){
                addUpdateTaskListSuccess()
            }
            else{
                Log.i("taskListUpdatedObserver","it == false")
            }
        })
    }

    private fun assignedMembersObserver() {
       var isNull = true;
        viewModel.assignedMemberDetailList.observe(this, androidx.lifecycle.Observer {
            if(it != null && it.isNotEmpty()){
                setUpSelectedMembersList()
                Log.i("assignedMembersObserver","first if call")
            }
            else{
                isNull = viewModel.checkAssignedMembers()
                if(isNull){
                    Log.i("assignedMembersObserver","assigned mems is empty or null ${viewModel.assignedMemberDetailList.value.toString()}")
                }
                else{
                    setUpSelectedMembersList()
                }
            }
        })
    }

    private fun setUpCardNameEt() {

        activityCardDetailsBinding.etNameCardDetails.setText(
            viewModel.boardDetails?.value
                ?.taskList?.get(viewModel.taskListPosition.value!!)
                ?.cards?.get(viewModel.cardPosition.value!!)
                ?.name
        )

        activityCardDetailsBinding.etNameCardDetails.setSelection(activityCardDetailsBinding
            .etNameCardDetails
            .text
            .toString()
            .length
        )

    }


    private fun setUpActionBar(){
        setSupportActionBar(activityCardDetailsBinding.toolbarCardDetailsActivity)

        activityCardDetailsBinding.toolbarCardDetailsActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = viewModel.boardDetails?.value?.taskList?.get(viewModel.taskListPosition.value!!)?.cards?.get(viewModel.cardPosition.value!!)?.name
        }

        activityCardDetailsBinding.toolbarCardDetailsActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            viewModel.setBoardDetails(intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!)
            Log.e("tag","boardDetails: ${viewModel.boardDetails.toString()}")
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            viewModel.setAssignedMembers(intent.getParcelableArrayListExtra<User>(Constants.BOARD_MEMBERS_LIST)!!)
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            viewModel.setTaskListPosition(intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1))
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            viewModel.setCardPosition(intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu);
        return super.onCreateOptionsMenu(menu)
    }

    fun addUpdateTaskListSuccess(){
        setResult(Activity.RESULT_OK);
        finish();
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteCard(
                    viewModel.boardDetails?.value
                        ?.taskList?.get(viewModel.taskListPosition.value!!)
                        ?.cards?.get(viewModel.cardPosition.value!!)
                        ?.name!!
                );
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun membersListDialog(){

        var cardAssignedMembersList = viewModel.boardDetails?.value
            ?.taskList
            ?.get(viewModel.taskListPosition.value!!)
            ?.cards
            ?.get(viewModel.cardPosition.value!!)
            ?.assignedTo

        if(cardAssignedMembersList!!.size > 0){
            for(i in viewModel.assignedMemberDetailList.value?.indices!!){
                for(j in cardAssignedMembersList){
                    if(viewModel.assignedMemberDetailList.value?.get(i)!!.id == j){
                        viewModel.assignedMemberDetailList.value?.get(i)!!.selected = true
                    }
                }
            }
        }
        else{
            for(i in viewModel.assignedMemberDetailList.value?.indices!!){
                viewModel.assignedMemberDetailList.value?.get(i)!!.selected = false
            }
        }

        val listDialog = object: MembersListDialog(this,
            viewModel.assignedMemberDetailList.value!!,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){

                    if(!viewModel.boardDetails?.value
                            ?.taskList
                            ?.get(viewModel.taskListPosition.value!!)
                            ?.cards
                            ?.get(viewModel.cardPosition.value!!)
                            ?.assignedTo
                            ?.contains(user.id)!!
                    ){
                        viewModel.boardDetails?.value
                            ?.taskList
                            ?.get(viewModel.taskListPosition.value!!)
                            ?.cards
                            ?.get(viewModel.cardPosition.value!!)
                            ?.assignedTo!!.add(user.id)
                    }

                }
                else{

                    viewModel.boardDetails?.value
                        ?.taskList
                        ?.get(viewModel.taskListPosition.value!!)
                        ?.cards
                        ?.get(viewModel.cardPosition.value!!)
                        ?.assignedTo
                        ?.remove(user.id)

                    for(i in viewModel.assignedMemberDetailList.value?.indices!!){
                        if(viewModel.assignedMemberDetailList.value!![i].id == user.id){
                            viewModel.assignedMemberDetailList.value!![i].selected = false
                        }
                    }

                }
                setUpSelectedMembersList()
            }

        }
        listDialog.show()

    }

    private fun setUpSelectedMembersList(){
        val cardAssignedMemberList = viewModel.boardDetails?.value
            ?.taskList
            ?.get(viewModel.taskListPosition.value!!)
            ?.cards
            ?.get(viewModel.cardPosition.value!!)
            ?.assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in viewModel.assignedMemberDetailList.value?.indices!!){
            for(j in cardAssignedMemberList!!){
                if(viewModel.assignedMemberDetailList.value!![i].id == j){
                    val selectedMember = SelectedMembers(
                        viewModel.assignedMemberDetailList.value!![i].id,
                        viewModel.assignedMemberDetailList.value!![i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            activityCardDetailsBinding.tvSelectMembers.visibility = View.GONE
            activityCardDetailsBinding.rvSelectedMembers.visibility = View.VISIBLE

            activityCardDetailsBinding.rvSelectedMembers.layoutManager = GridLayoutManager(this,6)
            val adapter = CardMembersListItemAdapter(this,selectedMembersList,true)
            activityCardDetailsBinding.rvSelectedMembers.adapter = adapter
            adapter.setOnClickListener(object : MemberItemClickInterface{
                override fun onClick() {
                    membersListDialog()
                }

            })

        }
        else{
            activityCardDetailsBinding.tvSelectMembers.visibility = View.VISIBLE;
            activityCardDetailsBinding.rvSelectedMembers.visibility = View.GONE;

        }

    }


    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this);
        builder.setTitle(resources.getString(R.string.alert));
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card,cardName))
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton(resources.getString(R.string.yes)){dialogInterface, which ->
            dialogInterface.dismiss();
            viewModel.deleteCard();

        }

        builder.setNegativeButton(resources.getString(R.string.no)){dialogInterface, which ->
            dialogInterface.dismiss();
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList;
    }

    private fun setColor(){
        activityCardDetailsBinding.tvSelectLabelColor.text = ""
        activityCardDetailsBinding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(viewModel.selectedColor.value.toString()))

    }

    private fun labelColorListDialog(){
        val colorsList : ArrayList<String> = colorsList();
        val listDialog = object: LabelColorListDialog(this,
            colorsList,resources.getString(R.string.str_select_label_color),
            viewModel.selectedColor.value.toString()){
            override fun onItemSelected(color: String) {
                viewModel.setSelectedColor(color)
                setColor()
            }

        }

        listDialog.show()

    }

    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if(dayOfMonth < 1) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                activityCardDetailsBinding.tvSelectDueDate.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)

                viewModel.setSelectedDueDate(theDate!!.time)
        },
            year,
            month,
            day
        );

        datePickerDialog.show()

    }

}