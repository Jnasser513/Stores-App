package com.nasser.storesapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.nasser.storesapp.databinding.ItemStoreBinding

class StoreAdapter {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemStoreBinding.bind(view)
    }

}