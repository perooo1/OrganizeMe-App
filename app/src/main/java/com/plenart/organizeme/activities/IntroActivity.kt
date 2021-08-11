package com.plenart.organizeme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.plenart.organizeme.R
import com.plenart.organizeme.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
   }

    private fun initListeners() {
        binding.btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignInIntro.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }


}