package com.webserveis.mysubscriptions

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/*
https://blog.teamtreehouse.com/making-sharedpreferences-easy-with-kotlin
https://blogs.naxam.net/sharedpreferences-made-easy-with-kotlin-generics-extensions-6189d8902fb0
 */
class Prefs(val context: Context) {
    val PREFS_FILENAME = BuildConfig.APPLICATION_ID + ".prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var menuShowDetail: Boolean
        get() = prefs.getBoolean("PREF_SHOW_DETAIL", false)
        set(value: Boolean) = prefs.edit().putBoolean("PREF_SHOW_DETAIL", value).apply()


    /*
       sharedDefaultPreferences
    */
    fun getDefaultSharedPreferencesName(): String? =  context.packageName + "_preferences"

    private val sharedDefaultPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val darkMode: Boolean
        get() = sharedDefaultPreferences.getBoolean("pref_dark_mode", false)

}