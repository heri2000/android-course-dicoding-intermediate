package com.rinjaninet.storyapp.components

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.rinjaninet.storyapp.R

class CustomEditText : AppCompatEditText {

    private var isPasswordConfirmation = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText)
        this.isPasswordConfirmation = styledAttributes.getBoolean(
            R.styleable.CustomEditText_isPasswordConfirmation, false
        )
        styledAttributes.recycle()
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {

        val styledAttributes = context.obtainStyledAttributes(
            attrs, R.styleable.CustomEditText, defStyleAttr, 0
        )
        this.isPasswordConfirmation = styledAttributes.getBoolean(
            R.styleable.CustomEditText_isPasswordConfirmation, false
        )
        styledAttributes.recycle()
        init()
    }

    private fun init() {
        when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME -> {
                hint = resources.getString(R.string.input_name)
            }
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                hint = resources.getString(R.string.input_email)
            }
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                hint = if (isPasswordConfirmation)
                    resources.getString(R.string.input_password_confirmation)
                else
                    resources.getString(R.string.input_password)
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        validateInput()
    }

    private fun validateInput() {
        when (inputType) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME -> {
                if (text.toString().isEmpty()) {
                    this.error = resources.getString(R.string.input_name)
                } else {
                    this.error = null
                }
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                if (text.toString().isEmpty()) {
                    this.error = resources.getString(R.string.input_email)
                } else {
                    if (!isValidEmailAddress(text.toString()))
                        this.error = resources.getString(R.string.invalid_email_address)
                    else
                        this.error = null
                }
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                if (text.toString().isEmpty()) {
                    if (isPasswordConfirmation) {
                        this.error = resources.getString(R.string.input_password_confirmation)
                    } else {
                        this.error = resources.getString(R.string.input_password)
                    }
                } else {
                    if (text.toString().length < 6)
                        this.error = resources.getString(R.string.password_6_characters_or_more)
                    else
                        this.error = null
                }

            }
        }
    }

    private fun isValidEmailAddress(emailAddress: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(emailAddress).matches()
    }

}