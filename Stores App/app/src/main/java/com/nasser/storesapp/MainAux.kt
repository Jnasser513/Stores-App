package com.nasser.storesapp

import com.nasser.storesapp.data.entity.Store

interface MainAux {
    fun hideFab(isVisible: Boolean = false)
    fun addStore(store: Store)
    fun updateStore(store: Store)
}