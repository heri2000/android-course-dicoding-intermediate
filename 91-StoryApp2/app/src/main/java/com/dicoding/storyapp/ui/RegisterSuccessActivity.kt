package com.dicoding.storyapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dicoding.storyapp.databinding.ActivityRegisterSuccessBinding
import com.dicoding.storyapp.network.ApiConfig
import com.dicoding.storyapp.network.LoginData
import com.dicoding.storyapp.network.LoginResponse
import com.dicoding.storyapp.preferences.LoginPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterSuccessBinding
    private var email: String? = null
    private var password: String? = null
    private lateinit var mLoginPreferences: LoginPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra(EXTRA_EMAIL)
        password = intent.getStringExtra(EXTRA_PASSWORD)

        mLoginPreferences = LoginPreferences(this)

        binding.btnRegisterSuccessContinue.setOnClickListener {
            login()
        }
    }

    private fun login() {
        binding.pbRegisterSuccessProgress.visibility = View.VISIBLE
        binding.btnRegisterSuccessContinue.isEnabled = false

        val service = ApiConfig.getApiService().login(
            LoginData(email ?: "", password ?: "")
        )
        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.error != null && !responseBody.error) {

                        if (responseBody.loginResult != null)
                            mLoginPreferences.setLogin(responseBody.loginResult)

                        val mainIntent = Intent(
                            this@RegisterSuccessActivity, MainActivity::class.java
                        )
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent)
                        finish()

                    } else {
                        gotoLogin()
                    }
                } else {
                    gotoLogin()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                gotoLogin()
            }
        })
    }

    private fun gotoLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASSWORD = "extra_password"
    }
}