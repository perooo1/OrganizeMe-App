package com.plenart.organizeme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : BaseActivity() {
    private lateinit var createBoardBinding: ActivityCreateBoardBinding;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding = ActivityCreateBoardBinding.inflate(layoutInflater);
        setContentView(createBoardBinding.root);

        setUpActionBar();

    }

    private fun setUpActionBar(){
        setSupportActionBar(createBoardBinding.toolbarCreateBoardActivity)

        createBoardBinding.toolbarCreateBoardActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        val actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
            actionBar.title = resources.getString(R.string.create_board_title);
        }
        createBoardBinding.toolbarCreateBoardActivity.setNavigationOnClickListener{
            onBackPressed();
        }
    }

}