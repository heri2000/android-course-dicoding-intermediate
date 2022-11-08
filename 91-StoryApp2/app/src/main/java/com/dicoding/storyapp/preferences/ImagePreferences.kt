package com.dicoding.storyapp.preferences

import android.content.Context

class ImagePreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveImageUrls(urls: ArrayList<String>) {
        val editor = preferences.edit()
        editor.apply {
            clear()
            putInt(COUNT, urls.size)
            for (i in urls.indices) {
                putString("img-$i", urls[i])
            }
            apply()
        }
    }

    fun getImageUrls(): ArrayList<String> {
        val list = arrayListOf<String>()
        preferences.apply {
            val count = getInt(COUNT, 0)
            for (i in 0 until count-1) {
                list.add(getString("img-$i", "").toString())
            }
        }
        return list
    }

    companion object {
        private const val PREFS_NAME = "image_pref"
        private const val COUNT = "count"
    }
}