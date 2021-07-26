package com.plenart.organizeme.activities

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMyProfileBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import com.plenart.organizeme.viewModels.MainActivityViewModel
import com.plenart.organizeme.viewModels.MyProfileViewModel
import kotlinx.coroutines.launch
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var myProfileBinding: ActivityMyProfileBinding;
    private lateinit var viewModel: MyProfileViewModel

    //private var mSelectedImageFileUri: Uri? = null;
    //private var mProfileImageURL: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater);
        setContentView(myProfileBinding.root);
        
        setUpActionBar();

        Log.i("MyProfileActivity", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(MyProfileViewModel(Application())::class.java)      //should work?

        initObservers();
        getValues();

        //Firestore().loadUserData(this);
        lifecycleScope.launch {
            viewModel.loadUserData()
        }

        myProfileBinding.ivUserImage.setOnClickListener {
            if(Constants.isReadExternalStorageAllowed(this)){
                Constants.showImageChooser(this);
            }
            else{
                Constants.requestStoragePermission(this);
            }
        }

        myProfileBinding.btnUpdateMyProfileActivity.setOnClickListener{
            if(viewModel.selectedImageFileUri?.value != null){
                viewModel.uploadUserImage();
            }
            if(viewModel.mobile.value.toString().isEmpty()){
                Toast.makeText(this,"Please provide a phone number",Toast.LENGTH_SHORT).show()
                Log.i("MyProfileActivity","please provide a phone number")
            }
            else{
                //showProgressDialog(resources.getString(R.string.please_wait));                //careful!
                viewModel.updateUserProfileData();
            }
        }

    }

    private fun getValues() {
        getName();
        getMobile();
    }

    private fun initObservers() {
        userObserver()
        nameObserver();
        mobileObserver();
        selectedImageFileUriObserver()
        updateUserProfileDataSuccessObserver()
    }

    private fun updateUserProfileDataSuccessObserver() {
        viewModel.updateUserProfileSuccess.observe(this, Observer {
            if(it){
                profileUpdateSuccess()
            }
            else{
                Log.i("successObserver","Update userprofile data not successful(false)");
            }
        })
    }

    private fun mobileObserver() {
        viewModel.mobile.observe(this, Observer {
            if(it == null){
                showErrorSnackBar("Please enter a mobile num")            //careful!
                Log.i("mobileObserver","Update mobile not successful");
            }
            else{
                viewModel.setMobile(it);
            }
        })
    }

    private fun nameObserver() {
        viewModel.name.observe(this, Observer { newName ->
            if(newName == null ){
                showErrorSnackBar("Please enter a name")            //careful!
            }
            else{
                myProfileBinding.etNameMyProfileActivity.text.toString().trim{it <=' '}
                viewModel.setName(newName);
            }
        })
    }

    private fun selectedImageFileUriObserver() {
        viewModel.selectedImageFileUri?.observe(this, Observer { newUri ->
            //TODO

        })
    }

    private fun userObserver() {
        Log.i("UserObserver","user observer function triggered")
        Log.i("UserObserver","user viewmodel object ${viewModel.user.value.toString()}")

        var isNull = true;
        viewModel.user?.observe(this, Observer { newUser ->
            if(newUser != null){
                setUserDataInUI()
                Log.i("UserObserver","user observer function triggered - first if call")
            }
            else{
                isNull = viewModel.checkUser()
                if(isNull){
                    Log.i("UserObserver","the else block")
                }
                else{
                    if (newUser != null) {
                        //updateNavigationUserDetails(newUser,true)
                        setUserDataInUI()
                    };
                }
            }
        } )
    }

    private fun getName() {                                                     //potential change to on text changed Listener
        myProfileBinding.etNameMyProfileActivity.doAfterTextChanged {
            viewModel.setName(it.toString())
        }
    }

    private fun getMobile() {                                                   //potential change to on text changed Listener
        myProfileBinding.etMobileMyProfileActivity.doAfterTextChanged {
            if(myProfileBinding.etMobileMyProfileActivity.text!!.isEmpty()){
                Toast.makeText(this,"1Please provide a phone number1",Toast.LENGTH_SHORT).show();
            }
            else{
                viewModel.setMobile(it.toString().toLong());
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            //mSelectedImageFileUri = data.data;
            //viewModel.selectedImageFileUri.value = data.data;
            viewModel.setSelectedImageFileUri(data.data)

            try{
                Glide.with(this@MyProfileActivity)
                    .load(viewModel.selectedImageFileUri?.value)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(myProfileBinding.ivUserImage);

            }
            catch (e: IOException){
                e.printStackTrace();
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(myProfileBinding.toolbarMyProfileActivity)

        myProfileBinding.toolbarMyProfileActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = resources.getString(R.string.my_profile_title);
        }
        myProfileBinding.toolbarMyProfileActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

    fun setUserDataInUI(){

        Glide.with(this@MyProfileActivity)
            .load(viewModel.user.value?.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(myProfileBinding.ivUserImage);

        myProfileBinding.etNameMyProfileActivity.setText(viewModel.user.value?.name);
        myProfileBinding.etEmailMyProfileActivity.setText(viewModel.user.value?.email)
        if(viewModel.user.value?.mobile != 0L){
            myProfileBinding.etMobileMyProfileActivity.setText(viewModel.user.value?.mobile.toString());
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
            Toast.makeText(this,"permission denied. You can change it in settings",Toast.LENGTH_LONG).show();
        }
    }

    /*
    fun getFileExtension(){
        Constants
    }
    */
/*
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait));
        if(mSelectedImageFileUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference
                .child("USER_IMAGE"+System.currentTimeMillis()
                        +"."+Constants.getFileExtension(this,mSelectedImageFileUri))

        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
            taskSnapshot ->
            Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString());

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mProfileImageURL = uri.toString();

                updateUserProfileData()

            }

        }.addOnFailureListener{
            exception ->
            Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show();
            hideProgressDialog();
        }

        }
    }
*/

    fun profileUpdateSuccess(){
        hideProgressDialog();
        setResult(Activity.RESULT_OK);
        finish();
    }

    /*
    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>();
        var changesMade: Boolean = false;

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL;
            changesMade = true;
        }

        if(myProfileBinding.etNameMyProfileActivity.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = myProfileBinding.etNameMyProfileActivity.text.toString();
            changesMade = true;
        }

        if(myProfileBinding.etMobileMyProfileActivity.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = myProfileBinding.etMobileMyProfileActivity.text.toString().toLong();
            changesMade = true;
        }

        if(changesMade){
            Firestore().updateUserProfileData(this, userHashMap);
            hideProgressDialog();
            changesMade = false;
        }

    }
    */

}