package com.geeksPro.teachersbook.data.local.pref

import android.content.Context

class PreferenceHelper(context: Context) {

    var sharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE)

    fun unit(context: Context) {
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    var saveBoolean
        set(value) = sharedPreferences?.edit()?.putBoolean("Bool", value!!)?.apply()!!
        get() = sharedPreferences?.getBoolean("Bool", false)

    fun getLanguage(): String? {
        return sharedPreferences.getString(LANGUAGE_KEY, "ky")
    }

    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(LANGUAGE_KEY, language).apply()
    }

    fun getSize(): String? {
        return sharedPreferences.getString(KEY_BOOLEAN_VALUE, "standard")
    }

    fun saveSize(size: String) {
        sharedPreferences.edit().putString(KEY_BOOLEAN_VALUE, size).apply()
    }


    companion object {
        private const val LANGUAGE_KEY = "language_key"
        private const val KEY_BOOLEAN_VALUE = "size_key"

    }
}