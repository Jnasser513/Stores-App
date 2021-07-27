package com.nasser.storesapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nasser.storesapp.classes.Store
import com.nasser.storesapp.databinding.ItemStoreBinding

class StoreAdapter(private var storeList: MutableList<Store>, private var listener: OnClickListener):
    RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    //Creacion de la vista para mostrar el item_store
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return ViewHolder(view)
    }

    //Visualizacion de las tiendas existentes en la base de datos
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = storeList.get(position)

        with(holder) {
            setListener(store)

            binding.textViewTittle.text = store.name
        }
    }

    override fun getItemCount(): Int = storeList.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemStoreBinding.bind(view)

        fun setListener(store: Store){
            binding.root.setOnClickListener {
                listener.onClick(store)
            }
        }
    }

}