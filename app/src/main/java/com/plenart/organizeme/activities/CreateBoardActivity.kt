package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityCreateBoardBinding
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.CreateBoardViewModel
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private lateinit var createBoardBinding: ActivityCreateBoardBinding;
    private lateinit var viewModel: CreateBoardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(createBoardBinding.root)

        setUpActionBar()

        Log.i("CreateBoardActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(CreateBoardViewModel::class.java)

        initObservers()
        getBoardName()

        if(intent.hasExtra(Constants.NAME)){
            viewModel.setUserName(intent.getStringExtra(Constants.NAME)!!)
        }

        createBoardBinding.ivBoardImageCreateBoardActivity.setOnClickListener {
            if(Constants.isReadExternalStorageAllowed(this)){
                Constants.showImageChooser(this)
            }
            else{
                Constants.requestStoragePermission(this)
            }
        }

        createBoardBinding.btnCreateCreateBoardActivity.setOnClickListener{
            if(viewModel.selectedImageFileUri?.value != null){
                viewModel.uploadBoardImage()
            }
            else{
                viewModel.createBoard()
            }
        }
    }

    private fun initObservers() {
        boardNameObserver()
        boardCreatedObserver()
    }

    private fun boardCreatedObserver() {
        viewModel.boardCreated.observe(this, Observer {
            if (it){
                boardCreatedSuccessfully()
            }
            else{
                Log.i("boardCreatedObserver","Board creation failed: it==false")
            }
        })
    }

    private fun boardNameObserver() {
        viewModel.boardName.observe(this, Observer {
            if(it == null || it.isEmpty()){
                Toast.makeText(this, "please provide a board name", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getBoardName(){
        createBoardBinding.etBoardNameCreateBoardActivity.doAfterTextChanged {
            viewModel.setBoardName(it.toString())
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setUpActionBar(){
        setSupportActionBar(createBoardBinding.toolbarCreateBoardActivity)

        createBoardBinding.toolbarCreateBoardActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        createBoardBinding.toolbarCreateBoardActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }
        else{
            Toast.makeText(this,"permission denied. You can change it in settings", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            viewModel.setSelectedImageFileUri(data.data);
            viewModel.setFileExtension(Constants.getFileExtension(this,data.data))

            try{
                Glide.with(this)
                    .load(viewModel.selectedImageFileUri?.value)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(createBoardBinding.ivBoardImageCreateBoardActivity)

            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

}