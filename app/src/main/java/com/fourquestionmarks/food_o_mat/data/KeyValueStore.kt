package com.fourquestionmarks.food_o_mat.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/*
    Wrapper Class for SharedPreferences that only provides methods really needed by our app for
    easier, safer usage.
 */
class KeyValueStore(  // Ref needed to access SharedPreferences
    private val app: Application
) {
    private val preferences: SharedPreferences
        get() = app.getSharedPreferences(KEY_VALUE_STORE_FILE_NAME, Context.MODE_PRIVATE)

    fun writeIntValue(key: String?, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun writeBoolValue(key: String?, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getIntValue(key: String?): Int {
        return preferences.getInt(key, DEFAULT_INT_VALUE)
    }

    fun getBoolValue(key: String?): Boolean {
        return preferences.getBoolean(key, DEFAULT_BOOL_VALUE)
    }

    companion object {
        private const val KEY_VALUE_STORE_FILE_NAME = "fom_settings"
        private const val DEFAULT_INT_VALUE = 0
        private const val DEFAULT_BOOL_VALUE = true
    }
}