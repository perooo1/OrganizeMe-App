package com.plenart.organizeme.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.plenart.organizeme.R
import com.plenart.organizeme.firebase.Firestore
import com.plenart.organizeme.fragments.IntroFragment
import com.plenart.organizeme.fragments.MainFragment
import com.plenart.organizeme.fragments.SplashFragment


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

       Handler(Looper.getMainLooper()).postDelayed({
           var currentUserID = Firestore().getCurrentUserID()

           if(currentUserID.isNotEmpty()){
               val fragment = MainFragment()
               supportFragmentManager.beginTransaction()
                   .add(R.id.main_content_navigation_component,fragment)
                   .commit()
           }
           else{
               val fragment = IntroFragment()
               supportFragmentManager.beginTransaction()
                   .add(R.id.main_content_navigation_component,fragment)
                   .commit()

           }
       },2500)



    }
}