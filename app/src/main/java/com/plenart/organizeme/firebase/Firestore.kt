package com.plenart.organizeme.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import kotlinx.coroutines.tasks.await

class Firestore {

    private val mFirestore = FirebaseFirestore.getInstance();

    fun registerUser(userInfo: User): Boolean{
        var registerSuccess = false

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                registerSuccess = true
                Log.d("registerUserFirestore","onSuccessListener called")
            }
            .addOnFailureListener {
                registerSuccess = false
                Log.d("registerUserFirestore","onFailureListener called")
            }
        return registerSuccess
    }

    fun getCurrentUserID(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun updateUserProfileData(userHashMap: HashMap<String, Any>): Boolean{
        var updateSuccess = false;

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i("updateProfileDataNEW","Profile data updated successfully")
                updateSuccess = true
            }.addOnFailureListener {
                    e ->
                Log.e("updateProfileDataNEW","Error while updating user data",e)
            }

        return updateSuccess;
    }

    suspend fun loadUserData(): User?{
        var loggedInUser: User? = null
        Log.i("loadUserDataNEW","First log - user is: $loggedInUser")

        try{
            val user = getUserFirestore()       //user callback function
            Log.i("loadUserDataNEW", " document(user) toString: ${user.toString()}")
            loggedInUser = user.toObject(User::class.java)
            Log.i("loadUserDataNEW","user object after joining: $loggedInUser")
        }
        catch(e: FirebaseFirestoreException){
            Log.e("loadUserDataNEW", "Error loading user data", e)
        }

        Log.i("loadUserDataNEW","last log - user is: $loggedInUser")
        return loggedInUser
    }

    private suspend fun getUserFirestore(): DocumentSnapshot {
        return mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .await()
    }

    fun createBoard(board:Board): Boolean{
        var boardCreated = false;

        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("createBoardNEW", "Board created successfully!")
                boardCreated = true
            }.addOnFailureListener {
                    exception ->
                boardCreated = false
                Log.i("createBoardNEW", "Board creation failed!")
            }
        return boardCreated
    }

    suspend fun getBoardsList(): ArrayList<Board>{
        val boardsList = ArrayList<Board>();
        Log.i("getBoardsListNEW","first log call: $boardsList")

        try{
            val boards = getBoardsListCallback()
            Log.i("getBoardsListNEW","query(boards) toString: ${boards.toString()}")
            for(i in boards.documents){
                val board = i.toObject(Board::class.java)!!
                board.documentID = i.id;
                boardsList.add(board)
            }
        }
        catch (e: FirebaseFirestoreException){
            Log.e("getBoardsListNEW", "Error loading boards List", e)
        }

        Log.i("getBoardsListNEW","last log call: $boardsList")
        return boardsList
    }

    private suspend fun getBoardsListCallback(): QuerySnapshot {
        return mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .await()
    }

    suspend fun getBoardDetails(documentID: String): Board? {
        var board: Board? = null
        Log.i("getBoardDetailsNEW","First log - board is : $board")

        try{
            val b = getBoardDetailsCallback(documentID)
            Log.i("getBoardDetailsNEW","document(board) toString: $b")
            board = b.toObject(Board::class.java)
            if (board != null) {
                board.documentID = b.id
            }
            Log.i("getBoardDetailsNEW","document(board) toString: $b")
        }
        catch (e: FirebaseFirestoreException){
            Log.e("getBoardDetailsNEW","Error getting board details", e)
        }

        Log.i("getBoardDetailsNEW","last log - board is: $board")
        return board
    }

    private suspend fun getBoardDetailsCallback(documentID: String): DocumentSnapshot{
        return mFirestore.collection(Constants.BOARDS)
            .document(documentID)
            .get()
            .await()
    }

    fun addUpdateTaskList(board: Board): Boolean{
        var addedUpdated = false

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i("addUpdateTaskListNEW", "TaskList updated successfully")
                addedUpdated = true
            }.addOnFailureListener {
                    exception ->
                addedUpdated = false
                Log.e("addUpdateTaskListNEW", "Error while creating a board", exception)
            }

        return addedUpdated
    }

    suspend fun getAssignedMembersListDetails(assignedTo: ArrayList<String>): ArrayList<User>{
        val assignedMembers = ArrayList<User>()
        Log.i("getAssignedMem","first log call: $assignedMembers")

        try {
            val members = getAssignedMembersListDetailsCallback(assignedTo)
            Log.i("getAssignedMem","query(members) toString $members")
            for(i in members.documents){
                val member = i.toObject(User::class.java)
                assignedMembers.add(member!!);
            }

        }
        catch (e: FirebaseFirestoreException){
            Log.e("getAssignedMem","error getting assigned mems",e)
        }

        Log.i("getAssignedMem","last log call: $assignedMembers")
        return assignedMembers
    }

    private suspend fun getAssignedMembersListDetailsCallback(assignedTo: ArrayList<String>): QuerySnapshot{
        return mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .await()
    }

    suspend fun getMemberDetails(email: String): User?{
        var user: User? = null;
        Log.i("getMemberDetailsNEW","first log - user is : $user")

        try{
            val u = getMemberDetailsCallback(email)
            Log.i("getMemberDetailsNEW","document(user) is $u")
            if(u.documents.size > 0 ){
                user = u.documents[0].toObject(User::class.java)
                Log.i("getMemberDetailsNEW","document(user) after joining is $user")
            }
        }
        catch (e: FirebaseFirestoreException){
            Log.e("getMemberDetailsNEW","error getting member details ", e)
        }

        return user
    }

    private suspend fun getMemberDetailsCallback(email: String): QuerySnapshot{
        return mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .await()
    }

    fun assignMemberToBoard(board: Board): Boolean{
        var success = false;

        val assignedToHashMap = HashMap<String,Any>();
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo;

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                success = true
            }.addOnFailureListener {
                    e ->
                success = false
                Log.e("assignMemberToBoardNEW","Error while creating a board", e)
            }
        return success
    }

}