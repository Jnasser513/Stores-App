package com.nasser.storesapp.classes

data class Store(val id: Long = 0,
                 var name: String,
                 var phone: String = "",
                 var website: String = "",
                 var isFavorite: Boolean = false)
