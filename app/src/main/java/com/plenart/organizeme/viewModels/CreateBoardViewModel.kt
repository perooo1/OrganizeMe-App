package com.plenart.organizeme.viewModels

import android.net.Uri
import android.util.Log
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

    private var selectedImageFileUri: Uri? = null
    private var userName: String = String()
    private var boardName: String = String()
    private var boardImageURL: String = String()
    private var fileExtension: String = String()
    private var boardCreated: Boolean = false

    val firestore = Firestore()

    fun setUserName(userName: String){
        this.userName = userName
    }

    fun setBoardName(boardName: String){
        this.boardName = boardName
    }

    fun getBoardName(): String{
        return this.boardName
    }

    fun setSelectedImageFileUri(uri: Uri?){
        selectedImageFileUri = uri
    }

    fun getSelectedImageFileUri(): Uri?{
        return selectedImageFileUri
    }

    fun setFileExtension(extension: String){
        fileExtension = extension
    }

    fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList();
        assignedUserArrayList.add(getCurrentUserID())

        if(boardName.isEmpty()){
            Log.i("createBoard","board name is empty")
        }
        else{
            val board = Board(
                boardName,
                boardImageURL,
                userName,
                assignedUserArrayList
            )
            boardCreated = firestore.createBoard(board)
        }
    }

    fun uploadBoardImage(){

        if(selectedImageFileUri != null){
            try{
                viewModelScope.launch {
                    val uploadedBoardImage = uploadBoardCallback()
                    uploadedBoardImage.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        boardImageURL = uri.toString()
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
                    +"."+ fileExtension)

        return sRef.putFile(selectedImageFileUri!!).await()
    }

    private fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

}