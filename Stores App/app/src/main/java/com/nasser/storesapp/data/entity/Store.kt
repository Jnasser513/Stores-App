package com.nasser.storesapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_table")
data class Store(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var phone: String ="",
    var website: String ="",
    var isFavorite: Boolean = false
)