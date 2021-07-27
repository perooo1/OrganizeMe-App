package com.plenart.organizeme.viewModels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.plenart.organizeme.R
import com.plenart.organizeme.activities.MyProfileActivity
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import kotlinx.coroutines.launch

class MyProfileViewModel(application: Application): AndroidViewModel(application) {

    private val _user: MutableLiveData<User> = MutableLiveData()
    private val _name: MutableLiveData<String> = MutableLiveData()
    private val _mobile: MutableLiveData<Long> = MutableLiveData()
    private val _selectedImageFileUri: MutableLiveData<Uri>? = MutableLiveData()
    private val _profileImageURL: MutableLiveData<String> = MutableLiveData()
    private val _updateUserProfileSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val firestore = Firestore()

    val user: LiveData<User>
        get() = _user

    val name: LiveData<String>
        get() = _name;

    val mobile: LiveData<Long>
        get() = _mobile;

    val selectedImageFileUri: LiveData<Uri>?
        get() = _selectedImageFileUri

    val profileImageURL: LiveData<String>
        get() = _profileImageURL;

    val updateUserProfileSuccess: LiveData<Boolean>
        get() = _updateUserProfileSuccess

    init {
        Log.i("MyProfileActivity", "MyProfileViewModel created!")
        viewModelScope.launch {
            loadUserData()
        }
    }

    private suspend fun loadUserData(){
        _user.value = firestore.loadUserDataNEW()
    }

    fun setName(name: String){
        _name.value = name;
    }

    fun setMobile(mobile: Long){
        _mobile.value = mobile;
    }

    fun setSelectedImageFileUri(uri: Uri?){
       _selectedImageFileUri!!.value = uri;
    }

    fun checkUser(): Boolean{
        return user == null
    }

    fun uploadUserImage(){
        //showProgressDialog(resources.getString(R.string.please_wait));
        if(_selectedImageFileUri?.value != null){
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference
                .child("USER_IMAGE"+System.currentTimeMillis()
                        +"."+
                        Constants.getFileExtension((getApplication<Application>().applicationContext) as MyProfileActivity, _selectedImageFileUri?.value))    //should work?

            sRef.putFile(_selectedImageFileUri.value!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString());

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    _profileImageURL.value = uri.toString();

                    updateUserProfileData()

                }

            }.addOnFailureListener{
                    exception ->
                Log.e("uploadUserImage","Error uploading user image", exception);
                //Toast.makeText(this,exception.message, Toast.LENGTH_LONG).show();
                //hideProgressDialog();
            }

        }
    }

    fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>();
        var changesMade: Boolean = false;

        if(_profileImageURL.value!!.isNotEmpty() && _profileImageURL.value != _user.value?.image){
            userHashMap[Constants.IMAGE] = _profileImageURL.value!!;
            changesMade = true;
        }

        if(_name.value != _user.value?.name){
            userHashMap[Constants.NAME] = _name.value.toString();
            changesMade = true;
        }

        if(_mobile.value != _user.value?.mobile){
            userHashMap[Constants.MOBILE] = _mobile.value.toString().toLong();
            changesMade = true;
        }

        if(changesMade){
            _updateUserProfileSuccess.value = firestore.updateUserProfileDataNEW(userHashMap)
            //Firestore().updateUserProfileData(this, userHashMap);
            //hideProgressDialog();
            changesMade = false;
        }

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MyProfileActivity", "MyProfileViewModel model destroyed!")
    }

}