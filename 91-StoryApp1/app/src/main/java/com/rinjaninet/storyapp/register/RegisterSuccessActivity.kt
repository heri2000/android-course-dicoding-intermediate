package com.rinjaninet.storyapp.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.rinjaninet.storyapp.MainActivity
import com.rinjaninet.storyapp.R

class RegisterSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_success)

        val btnContinue = findViewById<Button>(R.id.btn_register_success_continue)
        btnContinue.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            mainIntent.putExtra(MainActivity.EXTRA_TOKEN, "this is token")
            startActivity(mainIntent)
            finish()
        }
    }
}