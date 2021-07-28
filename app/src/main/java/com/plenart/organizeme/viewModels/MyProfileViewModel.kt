package com.plenart.organizeme.viewModels

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.storage.*
import com.plenart.organizeme.R
import com.plenart.organizeme.activities.MyProfileActivity
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyProfileViewModel: ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    private val _name: MutableLiveData<String> = MutableLiveData()
    private val _mobile: MutableLiveData<Long> = MutableLiveData()
    private val _selectedImageFileUri: MutableLiveData<Uri>? = MutableLiveData()
    private val _profileImageURL: MutableLiveData<String> = MutableLiveData()
    private val _updateUserProfileSuccess: MutableLiveData<Boolean> = MutableLiveData()
    private val _fileExtension: MutableLiveData<String>? = MutableLiveData()

    val firestore = Firestore()

    val user: LiveData<User>
        get() = _user

    val name: LiveData<String>
        get() = _name

    val mobile: LiveData<Long>
        get() = _mobile

    val selectedImageFileUri: LiveData<Uri>?
        get() = _selectedImageFileUri

    val profileImageURL: LiveData<String>
        get() = _profileImageURL

    val updateUserProfileSuccess: LiveData<Boolean>
        get() = _updateUserProfileSuccess

    val fileExtension: LiveData<String>?
        get() = _fileExtension

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
        _name.value = name
    }

    fun setFileExtension(extension: String?){
        Log.i("setFileExtension","setFileExtension called")
        _fileExtension?.value = extension
        Log.i("setFileExtension","setFileExtension: ${_fileExtension?.value}")
    }

    fun setMobile(mobile: Long){
        _mobile.value = mobile
    }

    fun setSelectedImageFileUri(uri: Uri?){
       _selectedImageFileUri!!.value = uri
    }

    fun checkUser(): Boolean{
        return user == null
    }

    fun uploadUserImage(){

        Log.i("UploadUserImage", "First call of a function")

        if(_selectedImageFileUri?.value != null){
            Log.i("UploadUserImage", "First if-log")
            try {
                viewModelScope.launch {
                    Log.i("UploadUserImage", "inside viewModelScope, before callback")
                    val uploadedImage = uploadUserImageCallback()

                    Log.i("UploadUserImage", "uploadedImage is : ${uploadedImage.toString()}")
                    Log.i("Firebase Image URL",uploadedImage.metadata!!.reference!!.downloadUrl.toString())

                    uploadedImage.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("UploadUserImage", "after second onSuccListener")
                        Log.i("UploadUserImage", "this is uri:$uri")
                        _profileImageURL.value = uri.toString()
                        Log.i("UploadUserImage", "after setting URL value: ${_profileImageURL.value}")

                        updateUserProfileData()
                    }
                }
            }
            catch (e: StorageException){
                Log.e("UploadUserImage","storage exception",e)
            }
        }

    }


    private suspend fun uploadUserImageCallback(): UploadTask.TaskSnapshot {
        val sRef: StorageReference = FirebaseStorage.getInstance()
            .reference
            .child("USER_IMAGE"+System.currentTimeMillis()
                    +"."+ _fileExtension?.value)
        Log.i("UploadUserImage", "this is CALLBACK fun")

        return sRef.putFile(_selectedImageFileUri?.value!!).await()

    }


    fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        var changesMade: Boolean = false
        Log.i("updateUserProfileData","_profileImageUrl is ${_profileImageURL.value.toString()}")
        Log.i("updateUserProfileData","_name is ${_name.value.toString()}")
        Log.i("updateUserProfileData","_mobile is ${_mobile.value.toString()}")

        if(_profileImageURL.value?.isNotEmpty() == true && _profileImageURL.value != _user.value?.image){
            userHashMap[Constants.IMAGE] = _profileImageURL.value!!
            changesMade = true
        }

        if(_name.value != _user.value?.name){
            userHashMap[Constants.NAME] = _name.value.toString()
            changesMade = true
        }

        if(_mobile.value != _user.value?.mobile){
            userHashMap[Constants.MOBILE] = _mobile.value.toString().toLong()
            changesMade = true
        }

        if(changesMade){
            _updateUserProfileSuccess.value = firestore.updateUserProfileDataNEW(userHashMap)
            Log.i("updateUserProfileData","User profile data updates Successfully!")
            //hideProgressDialog();
            changesMade = false;
        }

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MyProfileActivity", "MyProfileViewModel model destroyed!")
    }

}