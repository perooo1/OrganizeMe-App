package com.plenart.organizeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivitySplashBinding
import com.plenart.organizeme.databinding.FragmentSplashBinding
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.fragments.IntroFragment
import com.plenart.organizeme.fragments.MainFragment
import com.plenart.organizeme.fragments.SplashFragment
import com.plenart.organizeme.viewModels.MainActivityViewModel


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(savedInstanceState == null){
            val fragment = SplashFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.main_content_navigation_component,fragment)
                .commit()
        }

        /*
        binding = FragmentSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
         */
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        */

       Handler(Looper.getMainLooper()).postDelayed({
           
           var currentUserID = Firestore().getCurrentUserID()

           if(currentUserID.isNotEmpty()){
               val fragment = MainFragment()
               supportFragmentManager.beginTransaction()
                   .add(R.id.main_content_navigation_component,fragment)
                   .commit()

               //startActivity(Intent(this,MainActivity::class.java))

           }
           else{

               val fragment = IntroFragment()
               supportFragmentManager.beginTransaction()
                   .add(R.id.main_content_navigation_component,fragment)
                   .commit()


               //startActivity(Intent(this,IntroActivity::class.java))
           }
           
           //finish()
       },2500)



    }
}