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

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance();


    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess();
        }
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
                            activity.signInSuccess(loggedInUser)
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

    fun getBoardsList2(activity: TestRecyclerActivity){

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
                activity.displayRecyclerView(boardList);



            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName,"Error while creatng a board",e);
            }
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

    fun addUpdateTaskList(activity: TaskListActivity, board: Board){
        val taskListHashMap = HashMap<String, Any>();
        taskListHashMap[Constants.TASK_LIST] = board.taskList;

        mFirestore.collection(Constants.BOARDS)
            .document(board.documentID)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully");
                activity.addUpdateTaskListSuccess();
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName, "Error while creating a board", exception);
            }

    }

    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>){
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

                activity.setUpMembersList(usersList);

            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog();
                Log.e(activity.javaClass.simpleName, "error while getting assigned member",e);
            }
    }

}