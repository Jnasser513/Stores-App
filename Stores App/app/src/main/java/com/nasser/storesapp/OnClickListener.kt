package com.nasser.storesapp

import com.nasser.storesapp.data.entities.Store

interface OnClickListener {
    fun onClick(store: Store)
}