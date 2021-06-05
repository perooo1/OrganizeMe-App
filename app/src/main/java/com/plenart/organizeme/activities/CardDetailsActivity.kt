package com.plenart.organizeme.activities

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityCardDetailsBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.Card
import com.plenart.organizeme.models.Task
import com.plenart.organizeme.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var activityCardDetailsBinding: ActivityCardDetailsBinding;
    private lateinit var mBoardDetails : Board;
    private  var mTaskListPosition = -1;
    private  var mCardPosition = -1;

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

        activityCardDetailsBinding.btnUpdateCardDetails.setOnClickListener {
            if(activityCardDetailsBinding.etNameCardDetails.text.toString().isNotEmpty()){
                updateCardDetails();
            }
            else{
                Toast.makeText(this,"Please enter a card name", Toast.LENGTH_SHORT).show();
            }
        }

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
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        )
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

}