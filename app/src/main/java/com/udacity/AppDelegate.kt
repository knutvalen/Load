package com.udacity

import android.app.Application
import timber.log.Timber

class AppDelegate : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}