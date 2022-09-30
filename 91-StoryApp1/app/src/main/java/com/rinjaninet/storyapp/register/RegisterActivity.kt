package com.rinjaninet.storyapp.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rinjaninet.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            binding.apply {
                setEnableForm(false)
                tvRegisterError.text = ""
                tvRegisterError.visibility = View.GONE
                pbRegisterProgress.visibility = View.VISIBLE
            }

            val registerSuccessIntent = Intent(this, RegisterSuccessActivity::class.java)
            startActivity(registerSuccessIntent)

            finish()
        }
    }

    private fun setEnableForm(enable: Boolean) {
        binding.apply {
            edRegisterName.isEnabled = enable
            edRegisterEmail.isEnabled = enable
            edRegisterPassword.isEnabled = enable
            edRegisterPasswordConfirmation.isEnabled = enable
            btnRegister.isEnabled = enable
        }
    }
}