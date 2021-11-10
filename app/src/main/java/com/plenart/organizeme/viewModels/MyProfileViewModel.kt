package com.plenart.organizeme.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.storage.*
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyProfileViewModel: ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    private var name: String = String()
    private var mobile: Long = 0
    private var selectedImageFileUri: Uri? = null
    private var profileImageURL: String = String()
    private var updateUserProfileSuccess: Boolean = false
    private var fileExtension: String = String()

    val firestore = Firestore()

    val user: LiveData<User>
        get() = _user

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadUserData()
        }
    }

    private suspend fun loadUserData(){
        _user.postValue(firestore.loadUserData())
    }

    fun setName(name: String){
        this.name = name
    }

    fun setFileExtension(extension: String){
        fileExtension= extension
    }

    fun setMobile(mobile: Long){
        this.mobile = mobile
    }

    fun getMobile(): Long{
        return this.mobile
    }

    fun setSelectedImageFileUri(uri: Uri?){
       selectedImageFileUri = uri
    }

    fun getSelectedImageFileUri(): Uri? {
        return this.selectedImageFileUri
    }

    fun uploadUserImage(){
        if(selectedImageFileUri != null){
            try {
                viewModelScope.launch {
                    val uploadedImage = uploadUserImageCallback()
                    uploadedImage.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        profileImageURL = uri.toString()
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
                    +"."+ fileExtension)

        return sRef.putFile(selectedImageFileUri!!).await()
    }

    fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        var changesMade = false

        if(profileImageURL.isNotEmpty() && profileImageURL != _user.value?.image){
            userHashMap[Constants.IMAGE] = profileImageURL
            changesMade = true
        }

        if(name != _user.value?.name){
            userHashMap[Constants.NAME] = name
            changesMade = true
        }

        if(mobile != _user.value?.mobile){
            userHashMap[Constants.MOBILE] = mobile.toString().toLong()
            changesMade = true
        }

        if(changesMade){
            updateUserProfileSuccess = firestore.updateUserProfileData(userHashMap)
            changesMade = false;
        }
    }

}