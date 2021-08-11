package com.plenart.organizeme.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.storage.*
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
        _user.value = firestore.loadUserData()
    }

    fun setName(name: String){
        _name.value = name
    }

    fun setFileExtension(extension: String?){
        _fileExtension?.value = extension
    }

    fun setMobile(mobile: Long){
        _mobile.value = mobile
    }

    fun setSelectedImageFileUri(uri: Uri?){
       _selectedImageFileUri!!.value = uri
    }

    fun uploadUserImage(){
        if(_selectedImageFileUri?.value != null){
            try {
                viewModelScope.launch {
                    val uploadedImage = uploadUserImageCallback()
                    uploadedImage.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        _profileImageURL.value = uri.toString()
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

        return sRef.putFile(_selectedImageFileUri?.value!!).await()
    }

    fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        var changesMade: Boolean = false

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
            _updateUserProfileSuccess.value = firestore.updateUserProfileData(userHashMap)
            changesMade = false;
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MyProfileActivity", "MyProfileViewModel model destroyed!")
    }

}