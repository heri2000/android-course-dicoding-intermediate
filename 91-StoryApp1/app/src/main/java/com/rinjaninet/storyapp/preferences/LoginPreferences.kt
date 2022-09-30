package com.rinjaninet.storyapp.preferences

import android.content.Context
import com.rinjaninet.storyapp.login.LoginResult

class LoginPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setLogin(login: LoginResult) {
        val editor = preferences.edit()
        editor.apply {
            putString(NAME, login.name)
            putString(USER_ID, login.userId)
            putString(TOKEN, login.token)
            apply()
        }
    }

    fun getLogin(): LoginResult {
        preferences.apply {
            return LoginResult(
                getString(NAME, "").toString(),
                getString(USER_ID, "").toString(),
                getString(TOKEN, "").toString()
            )
        }
    }

    fun clearLogin() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "login_pref"
        private const val NAME = "name"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
    }

}