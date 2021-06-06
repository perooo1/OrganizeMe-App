package com.plenart.organizeme.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySplashBinding
import com.plenart.organizeme.firebase.FirestoreClass


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater);
        setContentView(binding.root);

        //setContentView(R.layout.activity_splash)                  deprecated


        //val typeFace: Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf");
        //binding.tvAppName.typeface = typeFace;


       Handler(Looper.getMainLooper()).postDelayed({
           
           var currentUserID = FirestoreClass().getCurrentUserID();

           if(currentUserID.isNotEmpty()){
               startActivity(Intent(this,MainActivity::class.java))
           }
           else{
               startActivity(Intent(this,IntroActivity::class.java))
           }
           
           finish();
       },2500)

    }
}