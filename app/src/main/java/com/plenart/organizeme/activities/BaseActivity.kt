package com.plenart.organizeme.activities

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.DialogProgressBinding

open abstract class BaseActivity : AppCompatActivity() {
    private lateinit var dialogProgressBinding: DialogProgressBinding
    private lateinit var mProgressDialog: Dialog

    private var doubleBackToExitPressedOnce = false

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        dialogProgressBinding = DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(dialogProgressBinding.root)

        dialogProgressBinding.tvProgressText.text = text

        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,R.string.please_click_back_again_to_exit,Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({doubleBackToExitPressedOnce = false},2000)

    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackBar.show()

    }

}