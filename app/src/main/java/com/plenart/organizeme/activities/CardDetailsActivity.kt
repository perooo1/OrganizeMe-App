package com.plenart.organizeme.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.plenart.organizeme.R
import com.plenart.organizeme.adapters.CardMembersListItemAdapter
import com.plenart.organizeme.databinding.ActivityCardDetailsBinding
import com.plenart.organizeme.dialogs.LabelColorListDialog
import com.plenart.organizeme.dialogs.MembersListDialog
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.interfaces.MemberItemClickInterface
import com.plenart.organizeme.models.*
import com.plenart.organizeme.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var activityCardDetailsBinding: ActivityCardDetailsBinding;
    private lateinit var mBoardDetails : Board;
    private lateinit var mMembersDetailList: ArrayList<User>;
    private  var mTaskListPosition = -1;
    private  var mCardPosition = -1;
    private var mSelectedColor = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCardDetailsBinding = ActivityCardDetailsBinding.inflate(layoutInflater);
        setContentView(activityCardDetailsBinding.root);

        getIntentData();
        setUpActionBar();

        activityCardDetailsBinding.etNameCardDetails.setText(mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .name)

        activityCardDetailsBinding.etNameCardDetails.setSelection(activityCardDetailsBinding
            .etNameCardDetails
            .text
            .toString()
            .length);

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor;
        if(mSelectedColor.isNotEmpty()){
            setColor();
        }

        activityCardDetailsBinding.btnUpdateCardDetails.setOnClickListener {
            if(activityCardDetailsBinding.etNameCardDetails.text.toString().isNotEmpty()){
                updateCardDetails();
            }
            else{
                Toast.makeText(this,"Please enter a card name", Toast.LENGTH_SHORT).show();
            }
        }

        activityCardDetailsBinding.tvSelectLabelColor.setOnClickListener {
            labelColorListDialog();
        }

        activityCardDetailsBinding.tvSelectMembers.setOnClickListener {
            membersListDialog();
        }

        setUpSelectedMembersList();

    }


    private fun setUpActionBar(){
        setSupportActionBar(activityCardDetailsBinding.toolbarCardDetailsActivity)

        activityCardDetailsBinding.toolbarCardDetailsActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name;
        }

        activityCardDetailsBinding.toolbarCardDetailsActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!;        //careful!
            Log.e("tag","mBoardDetails: ${mBoardDetails.toString()}")
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra<User>(Constants.BOARD_MEMBERS_LIST)!!;
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1);
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1);
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_delete_card,menu);

        return super.onCreateOptionsMenu(menu)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog();

        setResult(Activity.RESULT_OK);
        finish();

    }

    private fun updateCardDetails(){
        val card = Card(activityCardDetailsBinding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList;
        taskList.removeAt(taskList.size -1);

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card;
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails);


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card ->{
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name);
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards;
        cardsList.removeAt(mCardPosition);

        val taskList: ArrayList<Task> = mBoardDetails.taskList;
        taskList.removeAt(taskList.size - 1);

        taskList[mTaskListPosition].cards = cardsList;
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails);
    }

    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .assignedTo;

        if(cardAssignedMembersList.size > 0){
            for(i in mMembersDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true;
                    }
                }
            }
        }
        else{
            for(i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false;
            }
        }

        val listDialog = object: MembersListDialog(this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails
                            .taskList[mTaskListPosition]
                            .cards[mCardPosition]
                            .assignedTo
                            .contains(user.id)){
                        mBoardDetails
                            .taskList[mTaskListPosition]
                            .cards[mCardPosition]
                            .assignedTo.add(user.id);
                    }

                }
                else{
                    mBoardDetails
                        .taskList[mTaskListPosition]
                        .cards[mCardPosition]
                        .assignedTo
                        .remove(user.id);

                    for(i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id == user.id ){
                            mMembersDetailList[i].selected = false;
                        }
                    }

                }

                setUpSelectedMembersList();
            }

        }
        listDialog.show();


    }

    private fun setUpSelectedMembersList(){
        val cardAssignedMemberList = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .assignedTo;

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList();

        for(i in mMembersDetailList.indices){
            for(j in cardAssignedMemberList){
                if(mMembersDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    );
                    selectedMembersList.add(selectedMember);

                }
            }
        }

        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""));
            activityCardDetailsBinding.tvSelectMembers.visibility = View.GONE
            activityCardDetailsBinding.rvSelectedMembers.visibility = View.VISIBLE;

            activityCardDetailsBinding.rvSelectedMembers.layoutManager = GridLayoutManager(this,6);
            val adapter = CardMembersListItemAdapter(this,selectedMembersList,true);
            activityCardDetailsBinding.rvSelectedMembers.adapter = adapter;
            adapter.setOnClickListener(object : MemberItemClickInterface{
                override fun onClick() {
                    membersListDialog();
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
            deleteCard();

        }

        builder.setNegativeButton(resources.getString(R.string.no)){dialogInterface, which ->
            dialogInterface.dismiss();
        }

        val alertDialog: AlertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList();
        colorsList.add("#43C86F");
        colorsList.add("#0C90F1");
        colorsList.add("#F72400");
        colorsList.add("#7A8089");
        colorsList.add("#D57C1D");
        colorsList.add("#770000");
        colorsList.add("#0022F8");

        return colorsList;
    }

    private fun setColor(){
        activityCardDetailsBinding.tvSelectLabelColor.text = "";
        activityCardDetailsBinding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))

    }

    private fun labelColorListDialog(){
        val colorsList : ArrayList<String> = colorsList();
        val listDialog = object: LabelColorListDialog(this,
            colorsList,resources.getString(R.string.str_select_label_color),
            mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color;
                setColor();
            }

        }

        listDialog.show();

    }

}