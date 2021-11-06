package com.example.easyexchange

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class EasyExchangeApplication : Application() {

//    companion object {
//        lateinit var instance: EasyExchangeApplication
//            private set
//    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
//        instance = this
    }

}