package com.plenart.organizeme.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.plenart.organizeme.activities.MyProfileActivity

object Constants {
    const val USERS: String = "Users"
    const val BOARDS: String = "Boards"

    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"

    const val READ_STORAGE_PERMISSION_CODE = 1;
    const val PICK_IMAGE_REQUEST_CODE = 2;
    const val DOCUMENT_ID: String = "documentID";
    const val TASK_LIST: String = "taskList";
    const val BOARD_DETAIL: String = "board_detail";
    const val ID: String = "id";
    const val EMAIL: String = "email";
    const val BOARD_MEMBERS_LIST: String = "board_members_list";
    const val SELECT: String = "Select";
    const val UN_SELECT: String = "UnSelect";

    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position";
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position";

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    fun getFileExtension(activity:Activity, uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!));
    }

    fun isReadExternalStorageAllowed(activity: Activity): Boolean{
        val result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    fun requestStoragePermission(activity: Activity) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(activity, "Need permission to Change Profile Picture", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(activity,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE);
    }
}