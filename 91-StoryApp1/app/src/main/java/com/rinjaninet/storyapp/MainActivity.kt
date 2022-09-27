package com.rinjaninet.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra(EXTRA_EXIT, false)) {
            finish()
            return
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_EXIT = "extra_xit"
    }
}