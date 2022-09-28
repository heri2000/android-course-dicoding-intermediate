package com.rinjaninet.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rinjaninet.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            finish()
        }
    }
}