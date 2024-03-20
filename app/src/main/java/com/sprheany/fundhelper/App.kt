package com.sprheany.fundhelper

import android.app.Application
import com.sprheany.fundhelper.database.AppDatabase
import com.sprheany.fundhelper.workers.UpdateAllFundWorker

class App : Application() {

    val db: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this

        UpdateAllFundWorker.enqueue(this)
    }

    companion object {
        lateinit var instance: App
    }
}