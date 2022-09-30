package com.rinjaninet.storyapp.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rinjaninet.storyapp.MainActivity
import com.rinjaninet.storyapp.register.RegisterActivity
import com.rinjaninet.storyapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLoginRegisterHere.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        binding.btnLogin.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            mainIntent.putExtra(MainActivity.EXTRA_TOKEN, "this is token")
            startActivity(mainIntent)
            finish()
        }
    }

    // override fun onBackPressed() {
    //     super.onBackPressed()
    //     val exitIntent = Intent(this, MainActivity::class.java)
    //     exitIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    //     exitIntent.putExtra(MainActivity.EXTRA_EXIT, true)
    //     startActivity(exitIntent)
    // }
}