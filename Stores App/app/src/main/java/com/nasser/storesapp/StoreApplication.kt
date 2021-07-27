package com.nasser.storesapp

import android.app.Application
import androidx.room.Room
import com.nasser.storesapp.data.StoreDatabase

class StoreApplication: Application() {
    companion object{
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            StoreDatabase::class.java,
            "Store_db"
        ).build()
    }
}