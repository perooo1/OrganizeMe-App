package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityCreateBoardBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var createBoardBinding: ActivityCreateBoardBinding;

    private var mSelectedImageFileUri: Uri? =null;
    private var mUserName: String? = "";                                         //potential problem?
    private var mBoardImageURL: String = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding = ActivityCreateBoardBinding.inflate(layoutInflater);
        setContentView(createBoardBinding.root);

        setUpActionBar();

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME);                  //potential problem?
        }

        createBoardBinding.ivBoardImageCreateBoardActivity.setOnClickListener {
            if(Constants.isReadExternalStorageAllowed(this)){
                Constants.showImageChooser(this);
            }
            else{
                Constants.requestStoragePermission(this);
            }
        }

        createBoardBinding.btnCreateCreateBoardActivity.setOnClickListener{
            Log.i("create brd btn","click create brd btn");
            if(mSelectedImageFileUri != null){
                uploadBoardImage();
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait));
                createBoard();
            }


        }

    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog();
        finish();
    }

    private fun setUpActionBar(){
        setSupportActionBar(createBoardBinding.toolbarCreateBoardActivity)

        createBoardBinding.toolbarCreateBoardActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = resources.getString(R.string.create_board_title);
        }
        createBoardBinding.toolbarCreateBoardActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this);
            }
        }
        else{
            Toast.makeText(this,"permission denied. You can change it in settings", Toast.LENGTH_LONG).show();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data;

            try{
                Glide.with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(createBoardBinding.ivBoardImageCreateBoardActivity);

            }
            catch (e: IOException){
                e.printStackTrace();
            }
        }
    }

    private fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList();
        assignedUserArrayList.add(getCurrentUserID());

        var board = Board(createBoardBinding.etBoardNameCreateBoardActivity.text.toString(),
            mBoardImageURL,
            mUserName!!,
            assignedUserArrayList
        )

        FirestoreClass().createBoard(this,board);

    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait));
        if(mSelectedImageFileUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference
                .child("BOARD_IMAGE"+System.currentTimeMillis()
                        +"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase BoardImage URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString());

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downdlbl BoardImage URL", uri.toString())
                    mBoardImageURL = uri.toString();

                    createBoard();

                }

            }.addOnFailureListener{
                    exception ->
                Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }

        }
    }

}