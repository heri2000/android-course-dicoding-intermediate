package com.rinjaninet.storyapp.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rinjaninet.storyapp.R
import com.rinjaninet.storyapp.api.ApiConfig
import com.rinjaninet.storyapp.api.RegisterData
import com.rinjaninet.storyapp.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerNewUser()
        }
    }

    private fun registerNewUser() {
        binding.apply {
            enableFormElements(false)
            tvRegisterError.text = ""
            tvRegisterError.visibility = View.GONE

            val name = edRegisterName.text.toString().trim()
            val email = edRegisterEmail.text.toString().trim()
            val password = edRegisterPassword.text.toString()
            val passwordConfirmation = edRegisterPasswordConfirmation.text.toString()

            var isError = false
            if (name.isEmpty()) isError = true
            if (!isValidEmailAddress(email)) isError = true
            if (password.length < 6) isError = true
            if (passwordConfirmation != password) {
                isError = true
                edRegisterPasswordConfirmation.error = resources.getString(R.string.password_is_not_equal)
            }

            if (isError) {
                tvRegisterError.text = resources.getString(R.string.there_is_error)
                tvRegisterError.visibility = View.VISIBLE
                enableFormElements(true)
                return
            }

            val service = ApiConfig().getApiService().register(RegisterData(name, email, password))
            service.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {

                            val registerSuccessIntent = Intent(
                                this@RegisterActivity,
                                RegisterSuccessActivity::class.java
                            )
                            registerSuccessIntent.putExtra(
                                RegisterSuccessActivity.EXTRA_EMAIL, email
                            )
                            registerSuccessIntent.putExtra(
                                RegisterSuccessActivity.EXTRA_PASSWORD, password
                            )
                            startActivity(registerSuccessIntent)
                            finish()

                        } else {

                            tvRegisterError.text = responseBody?.message ?: resources.getString(
                                R.string.error_register_0102
                            )
                            tvRegisterError.visibility = View.VISIBLE
                            enableFormElements(true)
                            return

                        }
                    } else {

                        tvRegisterError.text = resources.getString(R.string.error_register_0101)
                        tvRegisterError.visibility = View.VISIBLE
                        enableFormElements(true)
                        return

                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    tvRegisterError.text = t.message
                    tvRegisterError.visibility = View.VISIBLE
                    enableFormElements(true)
                    return
                }
            })
        }
    }

    private fun enableFormElements(enable: Boolean) {
        binding.apply {
            edRegisterName.isEnabled = enable
            edRegisterEmail.isEnabled = enable
            edRegisterPassword.isEnabled = enable
            edRegisterPasswordConfirmation.isEnabled = enable
            btnRegister.isEnabled = enable
            pbRegisterProgress.visibility = if (enable) View.GONE else View.VISIBLE
        }
    }

    private fun isValidEmailAddress(emailAddress: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(emailAddress).matches()
    }

}