package com.plenart.organizeme.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.models.Board
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateBoardViewModel: ViewModel() {

    private val _selectedImageFileUri: MutableLiveData<Uri>? = MutableLiveData()
    private val _userName: MutableLiveData<String>? = MutableLiveData()
    private val _boardName: MutableLiveData<String> = MutableLiveData()
    private val _boardImageURL: MutableLiveData<String> = MutableLiveData()
    private val _fileExtension: MutableLiveData<String>? = MutableLiveData()
    private val _boardCreated: MutableLiveData<Boolean> = MutableLiveData()

    val firestore = Firestore()

    val selectedImageFileUri: LiveData<Uri>?
        get() = _selectedImageFileUri

    val userName: LiveData<String>?
        get() = _userName

    val boardName: LiveData<String>
        get() = _boardName

    val boardImageURL: LiveData<String>
        get() = _boardImageURL

    val fileExtension: LiveData<String>?
        get() = _fileExtension

    val boardCreated: LiveData<Boolean>
        get() = _boardCreated

    init {
        Log.i("CreateBoard", "CreateBoardViewModel created!")
    }

    fun setUserName(userName: String){
        _userName?.value = userName
    }

    fun setBoardName(boardName: String){
        _boardName.value = boardName
    }

    fun setSelectedImageFileUri(uri: Uri?){
        _selectedImageFileUri!!.value = uri
    }

    fun setFileExtension(extension: String?){
        Log.i("setFileExtension","setFileExtension called")
        _fileExtension?.value = extension
        Log.i("setFileExtension","setFileExtension: ${_fileExtension?.value}")
    }

    fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList();
        assignedUserArrayList.add(getCurrentUserID())

        val boardName = _boardName.value.toString()
        if(boardName.isEmpty()){
            Log.i("createBoard","board name is empty")
        }
        else{
            var board = Board(
                boardName,
                _boardImageURL.value.toString(),
                _userName?.value.toString(),
                assignedUserArrayList
            )
            _boardCreated.value = firestore.createBoard(board)
        }
    }

    fun uploadBoardImage(){

        if(_selectedImageFileUri?.value != null){
            try{
                viewModelScope.launch {
                    val uploadedBoardImage = uploadBoardCallback()
                    uploadedBoardImage.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        _boardImageURL.value = uri.toString()
                        createBoard()
                    }
                }
            }
            catch (e: StorageException) {
                Log.e("uploadBoardImage","upload board image error",e)
            }
        }

    }

    private suspend fun uploadBoardCallback(): UploadTask.TaskSnapshot{
        val sRef: StorageReference = FirebaseStorage.getInstance()
            .reference
            .child("BOARD_IMAGE"+System.currentTimeMillis()
                    +"."+ _fileExtension?.value)
        Log.i("uploadBoardCallback", "this is CALLBACK fun")

        return sRef.putFile(_selectedImageFileUri?.value!!).await()
    }

    private fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainActivityViewModel", "MainActivityViewModel model destroyed!")
    }
}