package com.geeksPro.teachersbook

import android.app.Application
import androidx.room.Room
import com.geeksPro.teachersbook.data.AppDataBase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext, AppDataBase::class.java, "database-name"
        ).allowMainThreadQueries().build()
    }

    companion object {
        lateinit var db: AppDataBase
    }
}