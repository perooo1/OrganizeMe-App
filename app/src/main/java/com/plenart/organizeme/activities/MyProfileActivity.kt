package com.plenart.organizeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityMyProfileBinding
import com.plenart.organizeme.databinding.AppBarMainBinding

class MyProfileActivity : BaseActivity() {
    private lateinit var myProfileBinding: ActivityMyProfileBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater);
        setContentView(myProfileBinding.root);


        setUpActionBar();

    }


    private fun setUpActionBar(){
        setSupportActionBar(myProfileBinding.toolbarMyProfileActivity)

        myProfileBinding.toolbarMyProfileActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = resources.getString(R.string.my_profile_title);
        }
        myProfileBinding.toolbarMyProfileActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

}