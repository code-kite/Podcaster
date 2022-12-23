package com.codebox.podcaster

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

import dagger.hilt.android.HiltAndroidApp


/**
 * Created by Codebox on 24/02/21
 */
private const val TAG = "PodcasterApp"

@HiltAndroidApp
class App : Application() {


    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        initialiseAppTheme()
    }

    private fun initialiseAppTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}