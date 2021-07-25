package com.plenart.organizeme.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.plenart.organizeme.activities.*
import com.plenart.organizeme.models.Board
import com.plenart.organizeme.models.User
import com.plenart.organizeme.utils.Constants
import kotlinx.coroutines.tasks.await

class Firestore {

    private val mFirestore = FirebaseFirestore.getInstance();


    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess();
        }
    }

    fun registerUserNEW(userInfo: User): Boolean{
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
        return registerSuccess;
    }

    fun getCurrentUserID(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser;
        var currentUserID = "";
        if(currentUser != null){
            currentUserID = currentUser.uid;
        }

        return currentUserID;
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile data updated successfully");
                Toast.makeText(activity,"Profile updated!", Toast.LENGTH_SHORT).show();
                activity.profileUpdateSuccess();
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName,"Error while updating user data",e);
                Toast.makeText(activity,"Error when updating user profile!", Toast.LENGTH_SHORT).show();
            }
    }


    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)

                when(activity){
                    is SignInActivity ->{
                        if (loggedInUser != null) {
                            activity.signInSuccess()            //deleted fun parameter
                        };
                    }
                    is MainActivity ->{
                        if (loggedInUser != null) {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        };
                    }
                    is MyProfileActivity ->{
                        if (loggedInUser != null) {
                            activity.setUserDataInUI(loggedInUser)
                        };
                    }
                }

            }.addOnFailureListener {

                    e ->
                Log.e("FirestoreSignInUser", "Error writing document", e);
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog();
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog();
                    }
                }
            }
    }

    suspend fun loadUserDataNEW(): User?{
        var loggedInUser: User? = null
        Log.i("loadUserDataNEW","First log - user is: $loggedInUser")

        try{
            val user = getUserFirestore()       //user callback function
            Log.i("loadUserDataNEW", " document(user) toString: ${user.toString()}");
            loggedInUser = user.toObject(User::class.java);
            Log.i("loadUserDataNEW","user object after joining: $loggedInUser")
        }
        catch(e: FirebaseFirestoreException){
            Log.e("loadUserDataNEW", "Error loading user data", e)
        }

        Log.i("loadUserDataNEW","last log - user is: $loggedInUser")
        return loggedInUser;
    }

    private suspend fun getUserFirestore(): DocumentSnapshot {
        return mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .await()
    }

    fun createBoard(activity: CreateBoardActivity, board:Board){
        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully!");
                Toast.makeText(activity,"Board created successfully!",Toast.LENGTH_SHORT).show();
                activity.boardCreatedSuccessfully();
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName,"Error while creating a board",exception);
            }
    }

    fun getBoardsList(activity: MainActivity){

        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString()); //logs correct data
                val boardList: ArrayList<Board> = ArrayList();

                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!;
                    board.documentID = i.id;
                    boardList.add(board);

                }
                activity.displayBoards(boardList);



            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName,"Error while creatng a board",e);
            }

    }

    suspend fun getBoardsListNEW(): ArrayList<Board>{
        val boardsList = ArrayList<Board>();
        Log.i("getBoardsListNEW","first log call: $boardsList")

        try{
            val boards = getBoardsListCallback()
            Log.i("getBoardsListNEW","query(boards) toString: ${boards.toString()}")
            for(i in boards.documents){
                val board = i.toObject(Board::class.java)!!
                board.documentID = i.id;
                boardsList.add(board);
            }
        }
        catch (e: FirebaseFirestoreException){
            Log.e("getBoardsListNEW", "Error loading boards List", e)
        }

        Log.i("getBoardsListNEW","last log call: $boardsList")
        return boardsList;
    }

    private suspend fun getBoardsListCallback(): QuerySnapshot {
        return mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .await();
    }

    fun getBoardDetails(activity: TaskListActivity, documentID: String) {
        mFirestore.collection(Constants.BOARDS)
            .document(documentID)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString());
                val board = document.toObject(Board::class.java)!!
                board.documentID = document.id;
                activity.boardDetails(board);

            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName,"Error while creatng a board",e);
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>();
        taskListHashMap[Constants.TASK_LIST] = board.taskList;

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully");
                if(activity is TaskListActivity)
                    activity.addUpdateTaskListSuccess();
                else
                    if(activity is CardDetailsActivity)
                        activity.addUpdateTaskListSuccess();
            }.addOnFailureListener {
                exception ->
                if(activity is TaskListActivity)
                    activity.hideProgressDialog();
                else
                    if(activity is CardDetailsActivity)
                        activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName, "Error while creating a board", exception);
            }
    }

    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>){
        mFirestore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString());

                val usersList: ArrayList<User> = ArrayList();

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user);
                }

                if(activity is MembersActivity){
                    activity.setUpMembersList(usersList);
                }
                else
                    if(activity is TaskListActivity){
                        activity.boardMembersDetailsList(usersList);
                    }


            }.addOnFailureListener {
                e ->
                if(activity is MembersActivity){
                    activity.hideProgressDialog()
                }
                else
                    if(activity is TaskListActivity){
                        activity.hideProgressDialog()
                    }
                Log.e(activity.javaClass.simpleName, "error while getting assigned member",e);
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!;
                    activity.memberDetails(user);
                }
                else{
                    activity.hideProgressDialog();
                    activity.showErrorSnackBar("No such member found!");
                }

            }.addOnFailureListener {
                e ->
                Log.e(activity.javaClass.simpleName, "Error while getting user details", e);
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){

        val assignedToHashMap = HashMap<String,Any>();
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo;

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user);
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
            }
    }

}