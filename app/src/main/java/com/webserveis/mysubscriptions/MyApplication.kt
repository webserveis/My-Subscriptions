package com.webserveis.mysubscriptions

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/*
https://proandroiddev.com/kotlin-clean-architecture-1ad42fcd97fa
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        prefs = Prefs(applicationContext)

        if (prefs.darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

    }

    companion object {
        lateinit var instance: MyApplication
            private set

        lateinit var prefs: Prefs
            private set

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}