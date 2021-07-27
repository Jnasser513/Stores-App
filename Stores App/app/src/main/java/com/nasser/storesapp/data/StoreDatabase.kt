package com.nasser.storesapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nasser.storesapp.data.dao.StoreDao
import com.nasser.storesapp.data.entities.Store

@Database(version = 1, entities = [Store::class])
abstract class StoreDatabase: RoomDatabase() {
    abstract fun storeDao(): StoreDao
}