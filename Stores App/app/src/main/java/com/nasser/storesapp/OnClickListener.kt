package com.nasser.storesapp

import com.nasser.storesapp.data.entity.Store


interface OnClickListener {
    fun onClick(store: Store)
    fun favoriteStore(store: Store)
    fun deleteStore(store: Store)
}