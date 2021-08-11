package com.plenart.organizeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.plenart.organizeme.databinding.ActivitySplashBinding
import com.plenart.organizeme.firebase.Firestore


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

       Handler(Looper.getMainLooper()).postDelayed({
           
           var currentUserID = Firestore().getCurrentUserID()

           if(currentUserID.isNotEmpty()){
               startActivity(Intent(this,MainActivity::class.java))
           }
           else{
               startActivity(Intent(this,IntroActivity::class.java))
           }
           
           finish()
       },2500)

    }
}