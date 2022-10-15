package com.rinjaninet.storyapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rinjaninet.storyapp.MainActivity
import com.rinjaninet.storyapp.R
import com.rinjaninet.storyapp.api.ApiConfig
import com.rinjaninet.storyapp.api.LoginData
import com.rinjaninet.storyapp.register.RegisterActivity
import com.rinjaninet.storyapp.databinding.ActivityLoginBinding
import com.rinjaninet.storyapp.preferences.LoginPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mLoginPreferences: LoginPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLoginPreferences = LoginPreferences(this)

        binding.tvLoginRegisterHere.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        binding.apply {
            enableFormElements(false)
            tvLoginError.text = ""
            tvLoginError.visibility = View.GONE

            val email = edLoginEmail.text.toString().trim()
            val password = edLoginPassword.text.toString()

            var isError = false
            if (!isValidEmailAddress(email)) isError = true
            if (password.length < 6) isError = true

            if (isError) {
                tvLoginError.text = resources.getString(R.string.there_is_error)
                tvLoginError.visibility = View.VISIBLE
                enableFormElements(true)
                return
            }

            val service = ApiConfig.getApiService().login(
                LoginData(email, password)
            )
            service.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.error != null && !responseBody.error) {

                            if (responseBody.loginResult != null)
                                mLoginPreferences.setLogin(responseBody.loginResult)

                            val mainIntent = Intent(
                                this@LoginActivity, MainActivity::class.java
                            )
                            mainIntent.flags =Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mainIntent)
                            finish()

                        } else {

                            tvLoginError.text = responseBody?.message ?: resources.getString(
                                R.string.error_login_0203
                            )
                            tvLoginError.visibility = View.VISIBLE
                            enableFormElements(true)
                            return

                        }
                    } else {

                        tvLoginError.text = resources.getString(R.string.error_login_0201)
                        tvLoginError.visibility = View.VISIBLE
                        enableFormElements(true)
                        return

                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    tvLoginError.text = resources.getString(R.string.error_login_0202)
                    tvLoginError.visibility = View.VISIBLE
                    enableFormElements(true)
                    return
                }
            })
        }
    }

    private fun enableFormElements(enable: Boolean) {
        binding.apply {
            edLoginEmail.isEnabled = enable
            edLoginPassword.isEnabled = enable
            btnLogin.isEnabled = enable
            tvLoginRegisterHere.isEnabled = enable
            pbLoginProgress.visibility = if (enable) View.GONE else View.VISIBLE
        }
    }

    private fun isValidEmailAddress(emailAddress: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(emailAddress).matches()
    }
}