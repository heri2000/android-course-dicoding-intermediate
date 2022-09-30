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
    // private lateinit var errorIcon: Drawable

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
        // errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_error_24) as Drawable
        // errorIcon = AppCompatResources.getDrawable(context, R.drawable.ic_error_24) as Drawable

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
                    // showErrorIcon(true)
                    // TooltipCompat.setTooltipText(
                    //     this, resources.getString(R.string.input_name)
                    // )
                } else {
                    this.error = null
                    // showErrorIcon(false)
                    // TooltipCompat.setTooltipText(this, "")
                }
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                if (text.toString().isEmpty()) {
                    this.error = resources.getString(R.string.input_email)
                    // showErrorIcon(true)
                    // TooltipCompat.setTooltipText(
                    //     this, resources.getString(R.string.input_email)
                    // )
                } else {
                    if (!isValidEmailAddress(text.toString()))
                        this.error = resources.getString(R.string.invalid_email_address)
                    else
                        this.error = null
                    // showErrorIcon(false)
                    // TooltipCompat.setTooltipText(this, "")
                }
            }

            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                if (text.toString().isEmpty()) {
                    // showErrorIcon(true)
                    if (isPasswordConfirmation) {
                        this.error = resources.getString(R.string.input_password_confirmation)
                        // TooltipCompat.setTooltipText(
                        //     this, resources.getString(R.string.input_password_confirmation)
                        // )
                    } else {
                        this.error = resources.getString(R.string.input_password)
                        // TooltipCompat.setTooltipText(
                        //     this, resources.getString(R.string.input_password)
                        // )
                    }
                } else {
                    if (text.toString().length < 6)
                        this.error = resources.getString(R.string.password_6_characters_or_more)
                    else
                        this.error = null
                    // showErrorIcon(false)
                    // TooltipCompat.setTooltipText(this, "")
                }

            }
        }
    }

    private fun isValidEmailAddress(emailAddress: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(emailAddress).matches()
    }

    // private fun showErrorIcon(show: Boolean) {
    //     if (show)
    //         setButtonDrawables(endOfTheText = errorIcon)
    //     else
    //         setButtonDrawables()
    // }

    // private fun setButtonDrawables(
    //     startOfTheText: Drawable? = null,
    //     topOfTheText: Drawable? = null,
    //     endOfTheText: Drawable? = null,
    //     bottomOfTheText: Drawable? = null
    // ) {
    //     setCompoundDrawablesWithIntrinsicBounds(
    //         startOfTheText,
    //         topOfTheText,
    //         endOfTheText,
    //         bottomOfTheText
    //     )
    // }

    // override fun onTouch(v: View?, event: MotionEvent?): Boolean {
    //     if (compoundDrawables[2] != null) {
    //         val errorIconStart: Float
    //         val errorIconEnd: Float
    //         var isErrorIconClicked = false
    //         if (layoutDirection == View.LAYOUT_DIRECTION_RTL && event != null) {
    //             errorIconEnd = (errorIcon.intrinsicWidth + paddingStart).toFloat()
    //             when {
    //                 event.x < errorIconEnd -> isErrorIconClicked = true
    //             }
    //         } else if (event != null) {
    //             errorIconStart = (width - paddingEnd - errorIcon.intrinsicWidth).toFloat()
    //             when {
    //                 event.x > errorIconStart -> isErrorIconClicked = true
    //             }
    //         }
    //         Log.i("AAAAA", isErrorIconClicked.toString())
    //         return if (isErrorIconClicked) {
    //             when (event?.action) {
    //                 MotionEvent.ACTION_UP -> {
    //                     this.performLongClick()
    //                     true
    //                 }
    //                 else -> false
    //             }
    //         } else {
    //             false
    //         }
    //     }
    //     return false
    // }

}