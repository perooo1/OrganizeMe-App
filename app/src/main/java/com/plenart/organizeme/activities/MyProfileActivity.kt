package com.plenart.organizeme.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMyProfileBinding
import com.plenart.organizeme.firebase.FirestoreClass
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var myProfileBinding: ActivityMyProfileBinding;
    private lateinit var mUserDetails: User;
    private var mSelectedImageFileUri: Uri? = null;
    private var mProfileImageURL: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater);
        setContentView(myProfileBinding.root);


        setUpActionBar();
        FirestoreClass().loadUserData(this);

        myProfileBinding.ivUserImage.setOnClickListener {
            if(Constants.isReadExternalStorageAllowed(this)){
                Constants.showImageChooser(this);
            }
            else{
                Constants.requestStoragePermission(this);
            }
        }

        myProfileBinding.btnUpdateMyProfileActivity.setOnClickListener{
            if(mSelectedImageFileUri != null){
                uploadUserImage();
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait));
                updateUserProfileData();
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri = data.data;

            try{
                Glide.with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
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

    fun setUserDataInUI(user: User){

        mUserDetails = user;

        Glide.with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(myProfileBinding.ivUserImage);

        myProfileBinding.etNameMyProfileActivity.setText(user.name);
        myProfileBinding.etEmailMyProfileActivity.setText(user.email)
        if(user.mobile != 0L){
            myProfileBinding.etMobileMyProfileActivity.setText(user.mobile.toString());
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



    fun profileUpdateSuccess(){
        hideProgressDialog();
        setResult(Activity.RESULT_OK);
        finish();
    }

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
            FirestoreClass().updateUserProfileData(this, userHashMap);
            hideProgressDialog();
            changesMade = false;
        }

    }


}