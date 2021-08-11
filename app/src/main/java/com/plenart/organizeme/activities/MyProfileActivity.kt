package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMyProfileBinding
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.MyProfileViewModel
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var myProfileBinding: ActivityMyProfileBinding
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(myProfileBinding.root)
        
        setUpActionBar()

        Log.i("MyProfileActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)

        initObservers()
        initListeners()
        getValues()

    }

    private fun initListeners() {
        myProfileBinding.ivUserImage.setOnClickListener {
            if(Constants.isReadExternalStorageAllowed(this)){
                Constants.showImageChooser(this)
            }
            else{
                Constants.requestStoragePermission(this)
            }
        }

        myProfileBinding.btnUpdateMyProfileActivity.setOnClickListener{
            if(viewModel.selectedImageFileUri?.value != null){
                viewModel.uploadUserImage()
            }
            if(viewModel.mobile.value.toString().isEmpty()){
                Toast.makeText(this,"Please provide a phone number",Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.updateUserProfileData()
            }
        }
    }

    private fun getValues() {
        getName()
        getMobile()
    }

    private fun initObservers() {
        initUser()
        initName()
        initMobile()
        initUpdateUserProfileDataSuccess()
    }

    private fun initUpdateUserProfileDataSuccess() {
        viewModel.updateUserProfileSuccess.observe(this, Observer {
            if(it){
                profileUpdateSuccess()
            }
            else{
                Log.i("successObserver","Update userprofile data not successful(it==false)")
            }
        })
    }

    private fun initMobile() {
        viewModel.mobile.observe(this, Observer {
            if(it == null){
                showErrorSnackBar("Please enter a mobile num")
            }
        })
    }

    private fun initName() {
        viewModel.name.observe(this, Observer { newName ->
            if(newName == null ){
                showErrorSnackBar("Please enter a name")
            }
        })
    }

    private fun initUser() {
        viewModel.user?.observe(this, Observer { newUser ->
            if(newUser != null){
                setUserDataInUI()
            }
            else{
                Log.i("UserObserver","error observing user")
            }
        } )
    }

    private fun getName() {
        myProfileBinding.etNameMyProfileActivity.addTextChangedListener {
            viewModel.setName(it.toString())
        }
    }

    private fun getMobile() {
        myProfileBinding.etMobileMyProfileActivity.doAfterTextChanged {
            if(myProfileBinding.etMobileMyProfileActivity.text!!.isEmpty()){
                Toast.makeText(this,"Please provide a phone number",Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.setMobile(it.toString().toLong())
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){

            viewModel.setSelectedImageFileUri(data.data)
            viewModel.setFileExtension(Constants.getFileExtension(this,data.data))

            try{
                Glide.with(this@MyProfileActivity)
                    .load(viewModel.selectedImageFileUri?.value)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(myProfileBinding.ivUserImage)

            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(myProfileBinding.toolbarMyProfileActivity)

        myProfileBinding.toolbarMyProfileActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        myProfileBinding.toolbarMyProfileActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun setUserDataInUI(){

        Glide.with(this@MyProfileActivity)
            .load(viewModel.user.value?.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(myProfileBinding.ivUserImage)

        myProfileBinding.etNameMyProfileActivity.setText(viewModel.user.value?.name)
        myProfileBinding.etEmailMyProfileActivity.setText(viewModel.user.value?.email)
        if(viewModel.user.value?.mobile != 0L){
            myProfileBinding.etMobileMyProfileActivity.setText(viewModel.user.value?.mobile.toString())
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
            Toast.makeText(this,"permission denied. You can change it in settings",Toast.LENGTH_LONG).show()
        }
    }

    private fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

}